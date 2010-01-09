(ns fleetdb.client-test
  (:require (fleetdb [client :as client]))
  (:use (clj-unit core)))

(defmacro with-client [name & body]
  `(let [~name (client/connect)]
     (try
       (~name ["delete" "elems"])
       ~@body
       (finally
         (client/close ~name)))))

(deftest "nondefault initialization options"
  (let [c (client/connect {:host "localhost" :port 3400})]
    (client/close c)))

(deftest "ping"
  (with-client client
    (assert= "pong" (client ["ping"]))))

(deftest "malformed query"
  (with-client client
    (assert-throws #"Malformed query"
      (client ["foo"]))))

(deftest "valid query"
  (with-client client
    (let [r1 (client ["insert" "elems" {"id" 1}])
          r2 (client ["select" "elems"])]
      (assert= r1 1)
      (assert= r2 [{"id" 1}]))))

(deftest "timeout"
  (assert-throws #"Read timed out"
    (let [client (client/connect {:port 3402 :timeout 1})]
      (client ["ping"]))))

(deftest "missing server"
  (assert-throws #"Connection refused"
    (client/connect {:port 3403})))

(deftest "auth success"
  (let [client (client/connect {:port 3401 :password "pass"})]
    (assert= "pong" (client ["ping"]))))

(deftest "auth ommission"
  (let [client (client/connect {:port 3401})]
    (assert-throws #"auth needed"
      (client ["ping"]))))

(deftest "auth failure"
  (assert-throws #"auth rejected"
    (client/connect {:port 3401 :password "wrong"})))

(deftest "client/query"
  (with-client client
    (assert= "pong" (client/query client ["ping"]))))

(deftest "client attrs"
  (with-client client
    (assert= 3400 (:port client))))
