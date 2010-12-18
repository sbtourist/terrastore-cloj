(ns terrastore.terrastore-helpers
 (:use
   [clojure.contrib.json :as json]
   )
 )

(defn as-string [value]
  (cond
    (string? value) value
    (map? value) (json/json-str value)
    :else (throw (IllegalArgumentException. "Wrong value type!"))
    )
  )

(defn as-map [value]
  (cond
    (map? value) value
    (string? value) (json/read-json value)
    :else (throw (IllegalArgumentException. "Wrong value type!"))
    )
  )