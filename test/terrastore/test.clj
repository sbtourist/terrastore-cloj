(ns terrastore.test (:use clojure.test terrastore.terrastore-cloj terrastore.terrastore-ops))

(deftest test-connection-exception
  (is (thrown? java.net.ConnectException (buckets "http://acme.org:8080")))
  )

(deftest test-terrastore-exception
  (try
    (get-value "http://127.0.0.1:8080" "test-terrastore-exception" "1")
    (is (false? true))
    (catch RuntimeException ex
      (is (not (nil? (.getMessage ex))))
      )
    (finally
      (remove-bucket "http://127.0.0.1:8080" "test-terrastore-exception")
      )
    )
  )

(deftest test-buckets
  (try
    (put-value "http://127.0.0.1:8080" "test-buckets" "1" "{\"key\" : \"value\"}")
    (def value (buckets "http://127.0.0.1:8080/"))
    (is (= "[\"test-buckets\"]" value))
    (finally
      (remove-bucket "http://127.0.0.1:8080" "test-buckets")
      )
    )
  )

(deftest test-values
  (try
    (put-value "http://127.0.0.1:8080" "test-values" "1" "{\"key1\" : \"value1\"}")
    (def value (values "http://127.0.0.1:8080" "test-values"))
    (is (= "{\"1\":{\"key1\" : \"value1\"}}" value))
    (finally
      (remove-bucket "http://127.0.0.1:8080" "test-values")
      )
    )
  )

(deftest test-put-value
  (try
    (put-value "http://127.0.0.1:8080" "test-put-value" "1" "{\"key\" : \"value\"}")
    (finally
      (remove-bucket "http://127.0.0.1:8080" "test-put-value")
      )
    )
  )

(deftest test-put-get-value
  (try
    (put-value "http://127.0.0.1:8080" "test-put-get-value" "1" "{\"key\" : \"value\"}")
    (def value (get-value "http://127.0.0.1:8080" "test-put-get-value" "1"))
    (is (= "{\"key\" : \"value\"}" value))
    (finally
      (remove-bucket "http://127.0.0.1:8080" "test-put-get-value")
      )
    )
  )

(deftest test-conditionally-put-get-value
  (try
    (conditionally-put-value "http://127.0.0.1:8080" "test-conditionally-put-get-value" "1" "{\"key1\" : \"value1\"}" {"predicate" "jxpath:/key1[.='value1']"})
    (conditionally-put-value "http://127.0.0.1:8080" "test-conditionally-put-get-value" "1" "{\"key2\" : \"value2\"}" {"predicate" "jxpath:/key1[.='value1']"})
    (def value (conditionally-get-value "http://127.0.0.1:8080" "test-conditionally-put-get-value" "1" {"predicate" "jxpath:/key2[.='value2']"}))
    (is (= "{\"key2\" : \"value2\"}" value))
    (finally
      (remove-bucket "http://127.0.0.1:8080" "test-conditionally-put-get-value")
      )
    )
  )

(deftest test-put-remove-value
  (try
    (put-value "http://127.0.0.1:8080" "test-put-remove-value" "1" "{\"key\" : \"value\"}")
    (remove-value "http://127.0.0.1:8080" "test-put-remove-value" "1")
    (finally
      (remove-bucket "http://127.0.0.1:8080" "test-put-remove-value")
      )
    )
  )

(deftest test-do-export-import
  (try
    (put-value "http://127.0.0.1:8080" "test-do-export-import" "1" "{\"key\" : \"value\"}")
    (do-export "http://127.0.0.1:8080" "test-do-export-import" {"destination" "export.json" "secret" "SECRET-KEY"})
    (do-import "http://127.0.0.1:8080" "test-do-export-import" {"source" "export.json" "secret" "SECRET-KEY"})
    (finally
      (remove-bucket "http://127.0.0.1:8080" "test-do-export-import")
      )
    )
  )

(deftest test-do-update
  (try
    (put-value "http://127.0.0.1:8080" "test-do-update" "1" "{\"key\" : \"value\"}")
    (def value (do-update "http://127.0.0.1:8080" "test-do-update" "1" "{\"updated\" : \"value\"}" {"function" "replace" "timeout" "3000"}))
    (is (= "{\"updated\":\"value\"}" value))
    (is (= "{\"updated\":\"value\"}" (get-value "http://127.0.0.1:8080" "test-do-update" "1")))
    (finally
      (remove-bucket "http://127.0.0.1:8080" "test-do-update")
      )
    )
  )

(deftest test-do-predicate-query
  (try
    (put-value "http://127.0.0.1:8080" "test-do-predicate-query" "1" "{\"key\" : \"value\"}")
    (def value (do-predicate-query "http://127.0.0.1:8080" "test-do-predicate-query" {"predicate" "jxpath:/key[.='value']"}))
    (is (= "{\"1\":{\"key\" : \"value\"}}" value))
    (finally
      (remove-bucket "http://127.0.0.1:8080" "test-do-predicate-query")
      )
    )
  )

(deftest test-do-range-query
  (try
    (put-value "http://127.0.0.1:8080" "test-do-range-query" "1" "{\"key1\" : \"value1\"}")
    (put-value "http://127.0.0.1:8080" "test-do-range-query" "2" "{\"key2\" : \"value2\"}")
    (def value (do-range-query "http://127.0.0.1:8080" "test-do-range-query" {"startKey" "1" "endKey" "2" "limit" "1"}))
    (is (= "{\"1\":{\"key1\" : \"value1\"}}" value))
    (finally
      (remove-bucket "http://127.0.0.1:8080" "test-do-range-query")
      )
    )
  )

(deftest test-complete
  (def terrastore-server (terrastore "http://127.0.0.1:8080"))
  (try
    (def test-bucket (terrastore-server :bucket "test-complete"))
    (def test-key (test-bucket :key "1"))
    (test-key :put "{\"key1\":\"value1\"}")
    (test-key :conditionally-put "{\"key1\":\"value1\"}" :params {"predicate" "jxpath:/key1[.='value1']"})
    (test-key :update :arguments "{\"key1\":\"value1\"}" :params {"function" "replace" "timeout" "3000"})
    (test-bucket :export :params {"destination" "export.json" "secret" "SECRET-KEY"})
    (test-bucket :import :params {"source" "export.json" "secret" "SECRET-KEY"})
    (is (= "{\"key1\":\"value1\"}" (test-key :get)))
    (is (= "{\"key1\":\"value1\"}" (test-key :conditionally-get :params {"predicate" "jxpath:/key1[.='value1']"})))
    (is (= "{\"1\":{\"key1\":\"value1\"}}" (test-bucket :query-by-range :params {"startKey" "1" "endKey" "1" "limit" "1"})))
    (is (= "{\"1\":{\"key1\":\"value1\"}}" (test-bucket :query-by-predicate :params {"predicate" "jxpath:/key1[.='value1']"})))
    (is (= "{\"1\":{\"key1\":\"value1\"}}" (test-bucket :list)))
    (finally
      ((terrastore-server :bucket "test-complete") :remove)
      )
    )
  )

