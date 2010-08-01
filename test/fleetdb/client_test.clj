(ns fleetdb.client-test
  (:require (fleetdb [client :as client]))
  (:import (java.io Closeable))
  (:use (clj-unit core)))

(defmacro with-client [[name opts] & body]
  (let [tname (with-meta name {:tag 'Closeable})]
    `(with-open [~tname (client/connect ~opts)]
       (~name ["delete" "elems"]))))

(deftest "default options"
  (with-open [#^Closeable client (client/connect)]))

(deftest "nondefault options"
  (with-open [#^Closeable client (client/connect {:port 3401})]))

(deftest "ping"
  (with-client [client nil]
    (assert= "pong" (client ["ping"]))))

(deftest "url options"
  (with-client [client {:url "fleetdb://:pass@localhost:3401"}]
    (assert= "pong" (client "ping"))))

(deftest "malformed query"
  (with-client [client nil]
    (assert-throws #"Malformed query"
      (client ["foo"]))))

(deftest "valid query"
  (with-client [client nil]
    (let [r1 (client ["insert" "elems" {"id" 1}])
          r2 (client ["select" "elems"])]
      (assert= r1 1)
      (assert= r2 [{"id" 1}]))))

(deftest "timeout"
  (assert-throws #"Read timed out"
    (with-client [client {:port 3402 :timeout 1}]
      (client ["ping"]))))

(deftest "missing server"
  (assert-throws #"Connection refused"
    (with-client [client {:port 3403}])))

(deftest "auth success"
  (with-client [client {:port 3401 :password "pass"}]
    (assert= "pong" (client ["ping"]))))

(deftest "auth ommission"
  (assert-throws #"auth needed"
    (with-client [client {:port 3401}]
      (client ["ping"]))))

(deftest "auth failure"
  (assert-throws #"auth rejected"
    (with-client [client {:port 3401 :password "wrong"}])))

(deftest "client/query"
  (with-client [client nil]
    (assert= "pong" (client/query client ["ping"]))))

(deftest "client attrs"
  (with-client [client nil]
    (assert= 3400 (:port client))))
