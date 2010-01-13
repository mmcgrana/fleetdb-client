; fleetdb-server -e -p 3400
; fleetdb-server -e -p 3401 -x pass
; while true; do nc -l 3402; done > /dev/null

(use 'clj-unit.core)
(set! *warn-on-reflection* true)
(require-and-run-tests
  'fleetdb.client-test)
