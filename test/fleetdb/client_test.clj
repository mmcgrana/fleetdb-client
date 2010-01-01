(ns fleetdb.client-test
  (:require (fleetdb [client :as client]))
  (:use (clj-unit core)))

(defmacro with-client [name & body]
  `(let [~name (client/connect "127.0.0.1" 3400)]
     (try
       (client/query ~name ["delete" "elems"])
       ~@body
       (finally
         (client/close ~name)))))

(deftest "ping"
  (with-client client
    (assert= "pong" (client/query client ["ping"]))))

(deftest "malformed query"
  (with-client client
    (assert-throws #"Malformed query"
      (client/query client ["foo"]))))

(deftest "valid query"
  (with-client client
    (let [r1 (client/query client ["insert" "elems" {"id" 1}])
          r2 (client/query client ["select" "elems"])]
      (assert= r1 1)
      (assert= r2 [{"id" 1}]))))
