# fleetdb-client

A Clojure client library for FleetDB.


## Installation

`fleetdb-client` is available via [Clojars](http://clojars.org); see the [project page](http://clojars.org/fleetdb-client) for instructions on installing with Leiningen or Maven.


## Usage

Usage of the library is simple:
 
    => (use 'fleetdb.client)
    => (def client (connect "127.0.0.1" 3400))
    
    => (query client ["ping"])
    "pong"

    => (query client ["select" "accounts" {"where" ["=" "id" 2]}])
    [{"id" 2 "owner" "Alice" "credits" 150}]
    
Keywords can be used in queries, as they are converted to strings before being sent to the server:

    => (query client [:select :accounts {:where [:= :id 2]}])
    [{"id" 2 "owner" "Alice" "credits" 150}]

The client will raise an exception in the case of an error:

    => (query client ["bogus"])
    java.lang.Exception: Malformed query: unrecognized query type '"bogus"'


## License

Copyright 2009 Mark McGranaghan and released under an MIT license.
