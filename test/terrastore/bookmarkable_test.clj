(ns terrastore.bookmarkable-test (:use clojure.test terrastore.terrastore-cloj))

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
    (is (= "{\"1\":{\"key1\":\"value1\"}}" (test-bucket :list :params {"limit" "1"})))
    (is (= "[\"test-complete\"]" (terrastore-server :buckets)))

    (finally
      ((terrastore-server :bucket "test-complete") :remove)
      )
    )
  )

