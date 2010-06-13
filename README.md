# Terrastore Clojure Client API

Terrastore Clojure Client provides easy to use APIs for accessing the [Terrastore](http://code.google.com/p/terrastore/) NOSQL store.
You can use it in two different ways:

    * Chainable.
    * Bookmarkable.

**Chainable** APIs can be used to interact with a Terrastore Server by chaining function calls as follows:

    ((((terrastore "http://127.0.0.1:8080") :bucket "bucket") :key "1") :get)

The most relevant part of the piece of code above is the _(((terrastore "http://127.0.0.1:8080") :bucket "bucket") :key "1")_ call which will access the Terrastore Server at _http://127.0.0.1:8080_, and the provided bucket (_bucket_) and key (_1_).

**Bookmarkable** APIs can be used to store a reference to a Terrastore Server connection, a bucket and a key, by simply "breaking" the chain above as follows:

    (def my-server (terrastore "http://127.0.0.1:8080"))
    (def my-bucket (my-server :bucket "bucket"))
    (def my-key (my-bucket :key "1"))

Once stored your references, you can pass them around to make your code more concise:

    (my-key :get)

Now, let's take a look at all supported operations.

## Bucket Management

We will use _bookmarkable_ APIs to maintain our samples as concise as possible, so let's bookmark our server and bucket:

    (def my-server (terrastore "http://127.0.0.1:8080"))
    (def my-bucket (my-server :bucket "bucket"))

Now let's see bucket management operations over our _my-bucket_ bucket.

### Remove Bucket.

Syntax:

    (my-bucket :remove)

### Export Bucket.

Syntax:

    (my-bucket :export :params {"destination" "..." "secret" "..."})

### Import Bucket.

Syntax:

    (my-bucket :import :params {"source" "..." "secret" "..."})

### List Values.

Syntax:

    (my-bucket :list :params {"limit" "..."})

### Predicate Queries

Syntax:

    (my-bucket :query-by-predicate :params {"predicate" "..."})

### Range Queries

Syntax:

    (my-bucket :query-by-range :params {"comparator" "..." "startKey" "..." "endKey" "..." "limit" "..." "predicate" "..." "timeToLive" "..."})

## Document Management

Now let's bookmark a key:

    (def my-key (my-bucket :key "1"))

And go on by showing document management operations over _my-key_.

### Put Document.

Syntax:

    (my-key :put "string containing a json document")

### Get Document.

Syntax:

    (my-key :get)

### Conditionally Put Document.

Syntax:

    (my-key :conditionally-put "string containing a json document" :params {"predicate" "..."})

### Conditionally Get Document.

Syntax:

    (my-key :conditionally-get :params {"predicate" "..."})

### Server-side Document Update.

Syntax:

    (my-key :update :arguments "string containing a json document representing update data" :params {"function" "..." "timeout" "..."})

## Where to find more.

You can find more detailed examples in the [test suite](http://github.com/sbtourist/terrastore-cloj/blob/master/test/terrastore/test.clj).
Moreover, take a look at the [Terrastore HTTP APIs guide](http://code.google.com/p/terrastore/wiki/HTTP_Client_API) for a more detailed description of all operations and their parameters. 
