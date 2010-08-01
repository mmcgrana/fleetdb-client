(ns fleetdb.client
  (:require (clj-json [core :as json]))
  (:import (java.net Socket URI)
           (java.io OutputStreamWriter BufferedWriter
                    InputStreamReader BufferedReader
                    Closeable)
           (clojure.lang IFn ILookup)))

(defn- doquery [#^BufferedWriter writer #^BufferedReader reader q]
  (let [#^String req (json/generate-string q)]
    (if-let [resp (do (.write writer req)
                      (.write writer "\r\n")
                      (.flush writer)
                      (.readLine reader))]
      (let [[status result] (json/parse-string resp)]
        (if (zero? status)
          result
          (throw (Exception. #^String result))))
      (throw (Exception. "No response from server.")))))

(defn query [client q]
  (doquery (:writer client) (:reader client) q))

(defn close [client]
  (.close #^BufferedReader (:reader client))
  (.close #^BufferedWriter (:writer client))
  (.close #^Socket         (:socket client)))

(defn- apply-url [url options]
  (let [url-parsed (URI. url)]
    (-> options
      (dissoc :url)
      (assoc :host (.getHost url-parsed))
      (assoc :port (.getPort url-parsed))
      (assoc :password (if-let [ui (.getUserInfo url-parsed)]
                         (second (re-find #":(.+)" ui)))))))

(defn connect [& [options]]
  (if-let [url (:url options)]
    (connect (apply-url url options))
    (let [host     (get options :host "127.0.0.1")
          port     (get options :port 3400)
          timeout  (get options :timeout)
          password (get options :password)
          socket   (Socket. #^String host #^Integer port)
          writer   (BufferedWriter. (OutputStreamWriter. (.getOutputStream  socket)))
          reader   (BufferedReader. (InputStreamReader.  (.getInputStream   socket)))
          attrs    {:writer writer :reader reader :socket socket
                    :host host :port port :password password :timeout timeout}]
      (when timeout
        (.setSoTimeout socket (int (* timeout 1000))))
      (when password
        (doquery writer reader ["auth" password]))
      (proxy [IFn ILookup Closeable] []
        (invoke [q] (doquery writer reader q))
        (valAt  [k] (attrs k))
        (close  []  (close attrs))))))
