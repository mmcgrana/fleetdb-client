(ns fleetdb.client
  (:require [clj-json :as json])
  (:import (java.net Socket)
           (java.io  OutputStreamWriter BufferedWriter
                     InputStreamReader BufferedReader)
           (clojure.lang IFn ILookup)))

(defn- doquery [#^BufferedWriter writer #^BufferedReader reader q]
  (.write writer #^String (json/generate-string q))
    (.write writer "\r\n")
    (.flush writer)
    (if-let [in-line (.readLine reader)]
      (let [[status result] (json/parse-string in-line)]
        (if (zero? status)
          result
          (throw (Exception. #^String result))))
      (throw (Exception. "No response from server."))))

(defn connect [& [options]]
  (let [host     (get options :host "127.0.0.1")
        port     (get options :port 3400)
        password (get options :password)
        socket   (Socket. #^String host #^Integer port)
        writer   (BufferedWriter. (OutputStreamWriter. (.getOutputStream  socket)))
        reader   (BufferedReader. (InputStreamReader.  (.getInputStream   socket)))
        attrs    {:writer writer :reader reader :socket socket
                  :host host :port port :password password}]
    (when password
      (doquery writer reader ["auth" password]))
    (proxy [IFn ILookup] []
      (invoke [q] (doquery writer reader q))
      (valAt  [k] (attrs k)))))

(defn query [client q]
  (doquery (:writer client) (:reader client) q))

(defn close [client]
  (.close #^BufferedReader (:reader client))
  (.close #^BufferedWriter (:writer client))
  (.close #^Socket         (:socket client)))

; (client/connect ...)
; (client/query client ...)
; (client/close client)
; (lib/query-thing client ...)
;   (client/query client ---)
; (client/query-foo client ...)
