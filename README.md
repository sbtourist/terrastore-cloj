# Terrastore Clojure Client API - Version 0.3.0

Terrastore Clojure Client provides easy to use APIs for accessing the [Terrastore](http://code.google.com/p/terrastore/) NOSQL store.
You can use it in two different ways:

* Bookmarkable.
* Nestable.

**Bookmarkable** APIs can be used to store a reference to a Terrastore Server connection, a bucket and a key, for later reuse:

    (def my-server (terrastore "http://127.0.0.1:8080"))
    (def my-bucket (my-server :bucket "bucket"))
    (def my-key (my-bucket :key "1"))

Once stored your references, you can pass them around to make your code more concise:

    (my-key :get)

**Nestable** APIs can be used to interact with a Terrastore Server in a DSL-style approach, by nesting function calls as follows:

    (with-terrastore "http://127.0.0.1:8080"
      (with-bucket "bucket"
        (with-key "1" :put "{\"key\":\"value\"}"
          )
        )
      )

As you may see, you just specify the Terrastore server, bucket and key you want to interact with, and the final operation, with related arguments, you want to execute.

Now, let's take a look at all supported operations in both flavors.

## Bucket Management

To maintain our samples about bookmarkable APIs as concise as possible, we'll bookmark our server and bucket as follows:

    (def my-server (terrastore "http://127.0.0.1:8080"))
    (def my-bucket (my-server :bucket "bucket"))

Now let's take a look at bucket management operations.

### Get Cluster Statistics.

Bookmarkable Syntax:

    (my-server :cluster-stats)

Nestable Syntax:

    (with-terrastore "http://127.0.0.1:8080" :cluster-stats)

### List All Buckets.

Bookmarkable Syntax:

    (my-server :buckets)

Nestable Syntax:

    (with-terrastore "http://127.0.0.1:8080" :buckets)

### Remove Bucket.

Bookmarkable Syntax:

    (my-bucket :remove)

Nestable Syntax:

    (with-terrastore "http://127.0.0.1:8080"
      (with-bucket "bucket" :remove
        )
      )

### Export Bucket.

Bookmarkable Syntax:

    (my-bucket :export :params {"destination" "..." "secret" "..."})

Nestable Syntax:

    (with-terrastore "http://127.0.0.1:8080"
      (with-bucket "bucket" :export :params {"destination" "..." "secret" "..."}
        )
      )

### Import Bucket.

Bookmarkable Syntax:

    (my-bucket :import :params {"source" "..." "secret" "..."})

Nestable Syntax:

    (with-terrastore "http://127.0.0.1:8080"
      (with-bucket "bucket" :import :params {"source" "..." "secret" "..."}
        )
      )

### List Values.

Bookmarkable Syntax:

    (my-bucket :list)
    (my-bucket :list :params {"limit" "..."})

Nestable Syntax:

    (with-terrastore "http://127.0.0.1:8080"
        (with-bucket "bucket" :list
          )
        )

    (with-terrastore "http://127.0.0.1:8080"
        (with-bucket "bucket" :list :params {"limit" "1"}
          )
        )

### Predicate Queries

Bookmarkable Syntax:

    (my-bucket :query-by-predicate :params {"predicate" "..."})

Nestable Syntax:

    (with-terrastore "http://127.0.0.1:8080"
        (with-bucket "bucket" :query-by-predicate :params {"predicate" "..."}
          )
        )

### Range Queries

Bookmarkable Syntax:

    (my-bucket :query-by-range :params {"comparator" "..." "startKey" "..." "endKey" "..." "limit" "..." "predicate" "..." "timeToLive" "..."})

Nestable Syntax:

    (with-terrastore "http://127.0.0.1:8080"
        (with-bucket "bucket" :query-by-range :params {"comparator" "..." "startKey" "..." "endKey" "..." "limit" "..." "predicate" "..." "timeToLive" "..."}
          )
        )

## Document Management

Now let's bookmark a key to use in our samples:

    (def my-key (my-bucket :key "1"))

And go on by showing document management operations in both bookmarkable and nestable flavors.

### Put Document.

Bookmarkable Syntax:

    (my-key :put "string containing a json document")

Nestable Syntax:

    (with-terrastore "http://127.0.0.1:8080"
        (with-bucket "bucket"
          (with-key "1" :put "string containing a json document"
            )
          )
        )

You can also represent and pass your document as a map object, rather than a string: it will be automatically converted.

### Get Document.

Bookmarkable Syntax:

    (my-key :get)

Nestable Syntax:

    (with-terrastore "http://127.0.0.1:8080"
        (with-bucket "bucket"
          (with-key "1" :get
            )
          )
        )

### Conditionally Put Document.

Bookmarkable Syntax:

    (my-key :conditionally-put "string containing a json document" :params {"predicate" "..."})

Nestable Syntax:

    (with-terrastore "http://127.0.0.1:8080"
        (with-bucket "bucket"
          (with-key "1" :conditionally-put "string containing a json document" :params {"predicate" "..."})
            )
          )
        )

You can also represent and pass your document as a map object, rather than a string: it will be automatically converted.

### Conditionally Get Document.

Bookmarkable Syntax:

    (my-key :conditionally-get :params {"predicate" "..."})

Nestable Syntax:

    (with-terrastore "http://127.0.0.1:8080"
        (with-bucket "bucket"
          (with-key "1" :conditionally-get :params {"predicate" "..."}
            )
          )
        )

### Server-side Document Update.

Bookmarkable Syntax:

    (my-key :update :arguments "string containing a json document representing update data" :params {"function" "..." "timeout" "..."})

Nestable Syntax:

    (with-terrastore "http://127.0.0.1:8080"
        (with-bucket "bucket"
          (with-key "1" :update :arguments "string containing a json document representing update data" :params {"function" "..." "timeout" "..."}
            )
          )
        )

You can also represent and pass your update data as a map object, rather than a string: it will be automatically converted.

## Where to find more.

You can find more detailed examples in the [test suite](http://github.com/sbtourist/terrastore-cloj/blob/master/test/terrastore/).
Moreover, take a look at the [Terrastore HTTP APIs guide](http://code.google.com/p/terrastore/wiki/HTTP_Client_API) for a more detailed description of all operations and their parameters.

## Download

You can download the latest jar distribution from [GitHub](http://github.com/sbtourist/terrastore-cloj/downloads) or [Clojars](http://clojars.org/terrastore-cloj).