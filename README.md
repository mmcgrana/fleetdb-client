# fleetdb-client

A Clojure client library for [FleetDB](http://fleetdb.org).


## Installation

First, download and start FleetDB as described in the [FleetDB getting started guide](http://fleetdb.org/docs/getting_started.html).

If you want to incorporate this client into a project managed by Maven or [Leiningen](http://github.com/technomancy/leiningen), you can find the necessary Maven artifact information on the client's [Clojars project page](http://clojars.org/fleetdb-client).

If you just want to get started right away with at the REPL:

    $ wget http://s3.amazonaws.com/fleetdb-client/fleetdb-client-standalone.jar
    $ java -cp fleetdb-client-standalone.jar clojure.contrib.repl_ln

## Usage

Using the library is simple:
 
    (use 'fleetdb.client)
    (def client (connect))
    
    (query client ["ping"])
    => "pong"

    (query client ["select" "accounts" {"where" ["=" "id" 2]}])
    => [{"id" 2 "owner" "Alice" "credits" 150}]
    
Keywords can be used in queries, as they are converted to strings before being sent to the server:

    (query client [:select :accounts {:where [:= :id 2]}])
    => [{"id" 2 "owner" "Alice" "credits" 150}]

The client can also be used as a function that executes the given query:

    (client [:ping])

The client will raise an exception in the case of an error:

    (client ["bogus"])
    java.lang.Exception: Malformed query: unrecognized query type '"bogus"'

You can optionally specify a host and port other than the default `"127.0.0.1"` and `3400`:

    (def client (connect {:host "68.127.150.103" :port 3401}))

## License

Copyright 2009 Mark McGranaghan and released under an MIT license.
