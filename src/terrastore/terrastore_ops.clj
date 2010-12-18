(ns terrastore.terrastore-ops 
  (:use 
    [clojure.java.io :as io] 
    [clojure.contrib.json :as json] 
    [http.async.client :as http]
    )
  (:refer-clojure :exclude [await]
    )
  )

(defn- strip-slash [base]
  (loop [url base]
    (if (= (- (.length url) 1) (.lastIndexOf url "/"))
      (recur (.substring url 0 (- (.length url) 1)))
      url
      )
    )
  )

(defn- prepare-body [value]
  (cond
    (string? value) value
    (map? value) (json/json-str value)
    :else (throw (IllegalArgumentException. "Wrong value type!"))
    )
  )

(defn- extract-status [response]
  (if
    (http/failed? response) (throw (http/error response))
    (http/status response)
    )
  )

(defn- extract-body [response]
  (if
    (http/failed? response) (throw (http/error response))
    (http/string response)
    )
  )

(defn- terrastore-error [base error]
  (if (seq error)
    (let [error-map (json/read-json error)]
      (throw (RuntimeException. (str "Message: " (error-map :message) " - Code: " (error-map :code))))
      )
    )
  )

(defn cluster-stats [base]
  (let [response (http/await (http/GET (str (strip-slash base) "/_stats/cluster")))]
    (cond
      (= ((extract-status response) :code) 200) (extract-body response)
      :else (terrastore-error base (extract-body response))
      )
    )
  )

(defn buckets [base]
  (let [response (http/await (http/GET (strip-slash base)))]
    (cond
      (= ((extract-status response) :code) 200) (extract-body response)
      :else (terrastore-error base (extract-body response))
      )
    )
  )

(defn values
  ([base bucket params]
    (let [url (str (strip-slash base) "/" bucket) 
          response (http/await (http/GET url :headers {"Content-Type" "application/json"} :query params))]
      (cond
        (= ((extract-status response) :code) 200) (extract-body response)
        :else (terrastore-error base (extract-body response))
        )
      )
    )
  ([base bucket]
    (values base bucket [])
    )
  )

(defn remove-bucket [base bucket]
  (let [url (str (strip-slash base) "/" bucket)
        response (http/await (http/DELETE url))]
    (cond
      (not (= ((extract-status response) :code) 204)) (terrastore-error base (extract-body response))
      )
    )
  )

(defn put-value [base bucket k v]
  (let [url (str (strip-slash base) "/" bucket "/" k)
        response (http/await (http/PUT url :headers {"Content-Type" "application/json"} :body (prepare-body v)))]
    (cond
      (not (= ((extract-status response) :code) 204)) (terrastore-error base (extract-body response))
      )
    )
  )

(defn conditionally-put-value [base bucket k v params]
  (let [url (str (strip-slash base) "/" bucket "/" k)
        response (http/await (http/PUT url :headers {"Content-Type" "application/json"} :query params :body (prepare-body v)))]
    (cond
      (not (= ((extract-status response) :code) 204)) (terrastore-error base (extract-body response))
      )
    )
  )

(defn get-value [base bucket k]
  (let [url (str (strip-slash base) "/" bucket "/" k)
        response (http/await (http/GET url :headers {"Content-Type" "application/json"}))]
    (cond
      (= ((extract-status response) :code) 200) (extract-body response)
      :else (terrastore-error base (extract-body response))
      )
    )
  )

(defn conditionally-get-value [base bucket k params]
  (let [url (str (strip-slash base) "/" bucket "/" k)
        response (http/await (http/GET url :headers {"Content-Type" "application/json"} :query params))]
    (cond
      (= ((extract-status response) :code) 200) (extract-body response)
      :else (terrastore-error base (extract-body response))
      )
    )
  )

(defn remove-value [base bucket k]
  (let [url (str (strip-slash base) "/" bucket "/" k)
        response (http/await (http/DELETE url))]
    (cond
      (not (= ((extract-status response) :code) 204)) (terrastore-error base (extract-body response))
      )
    )
  )

(defn do-export [base bucket params]
  (let [url (str (strip-slash base) "/" bucket "/export")
        response (http/await (http/POST url :query params))]
    (cond
      (not (= ((extract-status response) :code) 204)) (terrastore-error base (extract-body response))
      )
    )
  )

(defn do-import [base bucket params]
  (let [url (str (strip-slash base) "/" bucket "/import")
        response (http/await (http/POST url :query params))]
    (cond
      (not (= ((extract-status response) :code) 204)) (terrastore-error base (extract-body response))
      )
    )
  )

(defn do-update [base bucket k update params]
  (let [url (str (strip-slash base) "/" bucket "/" k "/update")
        response (http/await (http/POST url :headers {"Content-Type" "application/json"} :query params :body (prepare-body update)))]
    (cond
      (= ((extract-status response) :code) 200) (extract-body response)
      :else (terrastore-error base (extract-body response))
      )
    )
  )

(defn do-predicate-query [base bucket params]
  (let [url (str (strip-slash base) "/" bucket "/predicate")
        response (http/await (http/GET url :headers {"Content-Type" "application/json"} :query params))]
    (cond
      (= ((extract-status response) :code) 200) (extract-body response)
      :else (terrastore-error base (extract-body response))
      )
    )
  )

(defn do-range-query [base bucket params]
  (let [url (str (strip-slash base) "/" bucket "/range")
        response (http/await (http/GET url :headers {"Content-Type" "application/json"} :query params))]
    (cond
      (= ((extract-status response) :code) 200) (extract-body response)
      :else (terrastore-error base (extract-body response))
      )
    )
  )

(defn do-map-reduce [base bucket mapreduce]
  (let [url (str (strip-slash base) "/" bucket "/mapReduce")
        response (http/await (http/POST url :headers {"Content-Type" "application/json"} :body (prepare-body mapreduce)))]
    (cond
      (= ((extract-status response) :code) 200) (extract-body response)
      :else (terrastore-error base (extract-body response))
      )
    )
  )