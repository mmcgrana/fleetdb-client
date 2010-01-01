# fleetdb-client

A Clojure client library for FleetDB.

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
    

## Setup

If you are using [Leiningen](http//github.com/technomancy/leiningen), add `fleetdb-client` to your list of dependencies:

    :dependencies [[fleetdb-client "0.1.0-SNAPSHOT"] ...]

Then Leinegin will pull in the necessary jars when you run:
    
    $ lein deps

If you are using Maven, add the following to your configuration file:

    <repository>
      <id>clojars.org</id>
      <url>http://clojars.org/repo</url>
    </repository>
   
    <dependency>
      <groupId>fleetdb-client</groupId>
      <artifactId>fleetdb-client</artifactId>
      <version>0.1.0-SNAPSHOT</version>
    </dependency>

If you are managing your dependencies in some other way, the necessary jars are:

 * `fleetdb-client.jar`
 * [`clj-json.jar`](http://github.com/mmcgrana/clj-json)
 * [`jackson-core-asl-1.4.0.jar`](http://jackson.codehaus.org/)

## License

Copyright 2009 Mark McGranaghan and released under an MIT license.
