(use 'clj-unit.core)
(set! *warn-on-reflection* true)
(require-and-run-tests
  'fleetdb.client-test)
