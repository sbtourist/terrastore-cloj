(ns terrastore.terrastore-cloj (:use matchure terrastore.terrastore-ops))

(defn- key-operations [base bucket k]
  (fn [operation & args]
    ((fn-match key-operations-match
       ([:put] (put-value base bucket k (first args)))
       ([:get] (get-value base bucket k))
       ([:remove] (remove-value base bucket k))
       ([:conditionally-put] (let [operation-args (apply hash-map (rest args))] (conditionally-put-value base bucket k (first args) (operation-args :params))))
       ([:conditionally-get] (let [operation-args (apply hash-map args)] (conditionally-get-value base bucket k (operation-args :params))))
       ([:update] (let [operation-args (apply hash-map args)] (do-update base bucket k (operation-args :arguments) (operation-args :params))))
       ) operation)
    )
  )

(defn- bucket-operations [base bucket]
  (fn [operation & args]
    ((fn-match bucket-operations-match
       ([:list] (let [operation-args (apply hash-map args)] (values base bucket (if (nil? (seq operation-args)) {} (operation-args :params)))))
       ([:remove] (remove-bucket base bucket))
       ([:import] (let [operation-args (apply hash-map args)] (do-import base bucket (operation-args :params))))
       ([:export] (let [operation-args (apply hash-map args)] (do-export base bucket (operation-args :params))))
       ([:query-by-predicate] (let [operation-args (apply hash-map args)] (do-predicate-query base bucket (operation-args :params))))
       ([:query-by-range] (let [operation-args (apply hash-map args)] (do-range-query base bucket (operation-args :params))))
       ([:key] (key-operations base bucket (first args)))
       ) operation)
    )
  )

(defn terrastore [base]
  (fn [operation & args]
    ((fn-match base-match
      ([:cluster-stats] (cluster-stats base))
      ([:buckets] (buckets base))
      ([:bucket] (bucket-operations base (first args)))
      ) operation)
    )
  )

(defmacro with-terrastore [base & forms]
  (cond (seq? (first forms))
    `(let [~'terrastore_cloj-server (terrastore ~base)] (do ~@forms))
    :else
    `(let [args# (list ~@forms)] (apply (terrastore ~base) args#))
    )
  )

(defmacro with-bucket [bucket & forms]
  (cond (seq? (first forms))
    `(let [~'terrastore_cloj-bucket (~'terrastore_cloj-server :bucket ~bucket)] (do ~@forms))
    :else
    `(let [args# (list ~@forms)] (apply (~'terrastore_cloj-server :bucket ~bucket) args#))
    )
  )

(defmacro with-key [k & forms]
  `(let [args# (list ~@forms)] (apply (~'terrastore_cloj-bucket :key ~k) args#))
  )