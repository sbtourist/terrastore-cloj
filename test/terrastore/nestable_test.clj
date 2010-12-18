(ns terrastore.nestable-test (:use [clojure.test] [clojure.contrib.str-utils2 :only (contains?)] [terrastore.terrastore-cloj]))

(deftest test-nestable
  (def terrastore-server (terrastore "http://127.0.0.1:8080"))

  (try
    (with-terrastore "http://127.0.0.1:8080"
      (with-bucket "test-macros"
        (with-key "1" :put "{\"key1\":\"value1\"}"
          )
        )
      )

    (with-terrastore "http://127.0.0.1:8080"
      (with-bucket "test-macros"
        (with-key "1" :conditionally-put "{\"key1\":\"value1\"}" :params {"predicate" "jxpath:/key1[.='value1']"}
          )
        )
      )

    (with-terrastore "http://127.0.0.1:8080"
      (with-bucket "test-macros" :export :params {"destination" "export.json" "secret" "SECRET-KEY"}
        )
      )

    (with-terrastore "http://127.0.0.1:8080"
      (with-bucket "test-macros" :import :params {"source" "export.json" "secret" "SECRET-KEY"}
        )
      )

    (is (= "{\"key1\":\"value1\"}"
          (with-terrastore "http://127.0.0.1:8080"
            (with-bucket "test-macros"
              (with-key "1" :update :arguments "{\"key1\":\"value1\"}" :params {"function" "replace" "timeout" "3000"}
                )
              )
            )
          )
      )

    (is (= "{\"key1\":\"value1\"}"
          (with-terrastore "http://127.0.0.1:8080"
            (with-bucket "test-macros"
              (with-key "1" :get
                )
              )
            )
          )
      )

    (is (= "{\"key1\":\"value1\"}"
          (with-terrastore "http://127.0.0.1:8080"
            (with-bucket "test-macros"
              (with-key "1" :conditionally-get :params {"predicate" "jxpath:/key1[.='value1']"}
                )
              )
            )
          )
      )

    (is (= "{\"1\":{\"key1\":\"value1\"}}"
          (with-terrastore "http://127.0.0.1:8080"
            (with-bucket "test-macros" :query-by-range :params {"startKey" "1" "endKey" "1" "limit" "1"}
              )
            )
          )
      )

    (is (= "{\"1\":{\"key1\":\"value1\"}}"
          (with-terrastore "http://127.0.0.1:8080"
            (with-bucket "test-macros" :query-by-predicate :params {"predicate" "jxpath:/key1[.='value1']"}
              )
            )
          )
      )

    (is (= "{\"size\":1}"
          (with-terrastore "http://127.0.0.1:8080"
            (with-bucket "test-macros" :query-by-map-reduce :descriptor {:task {:mapper "size" :reducer "size" :timeout 10000}}
              )
            )
          )
      )

    (is (= "{\"1\":{\"key1\":\"value1\"}}"
          (with-terrastore "http://127.0.0.1:8080"
            (with-bucket "test-macros" :list
              )
            )
          )
      )

    (is (= "{\"1\":{\"key1\":\"value1\"}}"
          (with-terrastore "http://127.0.0.1:8080"
            (with-bucket "test-macros" :list :params {"limit" "1"}
              )
            )
          )
      )

    (is (= "[\"test-macros\"]"
          (with-terrastore "http://127.0.0.1:8080" :buckets)
          )
      )
    
    (is (= true (contains?
          (with-terrastore "http://127.0.0.1:8080" :cluster-stats)
          "8080"
          ))
      )

    (finally
      ((terrastore-server :bucket "test-macros") :remove)
      )
    )
  )
