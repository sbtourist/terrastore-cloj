(ns terrastore.terrastore-cloj (:use matchure terrastore.terrastore-ops))

(defn key-operations [base bucket k]
  (fn [operation & args]
    ((fn-match key-operations-match
       ([:put] (put-value base bucket k (first args)))
       ([:get] (get-value base bucket k))
       ([:remove] (remove-value base bucket k))
       ([:conditionally-put] (do (def operation-args (apply hash-map (rest args))) (conditionally-put-value base bucket k (first args) (operation-args :params))))
       ([:conditionally-get] (do (def operation-args (apply hash-map args)) (conditionally-get-value base bucket k (operation-args :params))))
       ([:update] (do (def operation-args (apply hash-map args)) (do-update base bucket k (operation-args :arguments) (operation-args :params))))
       ) operation)
    )
  )

(defn bucket-operations [base bucket]
  (fn [operation & args]
    ((fn-match bucket-operations-match
       ([:list] (values base bucket))
       ([:remove] (remove-bucket base bucket))
       ([:import] (do (def operation-args (apply hash-map args)) (do-import base bucket (operation-args :params))))
       ([:export] (do (def operation-args (apply hash-map args)) (do-export base bucket (operation-args :params))))
       ([:query-by-predicate] (do (def operation-args (apply hash-map args)) (do-predicate-query base bucket (operation-args :params))))
       ([:query-by-range] (do (def operation-args (apply hash-map args)) (do-range-query base bucket (operation-args :params))))
       ([:key] (key-operations base bucket (first args)))
       ) operation)
    )
  )

(defn terrastore [base]
  (fn [operation & args]
    ((fn-match base-match
      ([:list] (buckets base))
      ([:bucket] (bucket-operations base (first args)))
      ) operation)
    )
  )