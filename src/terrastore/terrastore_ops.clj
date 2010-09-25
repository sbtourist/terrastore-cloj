(ns terrastore.terrastore-ops (:use clojure.contrib.json clojure-http.client))

(defn- strip-slash [base]
  (loop [url base]
    (if (= (- (.length url) 1) (.lastIndexOf url "/"))
      (recur (.substring url 0 (- (.length url) 1)))
      url
      )
    )
  )

(defn- extract-body [response]
  (apply str (response :body-seq))
  )

(defn- terrastore-error [base error]
  (if (seq error)
    (let [error-map (read-json error)]
      (throw (RuntimeException. (str "Message: " (error-map "message") " - Code: " (error-map "code"))))
      )
    )
  )

(defn buckets [base]
  (let [response (request (strip-slash base) "GET")]
    (cond
      (= (response :code) 200) (extract-body response)
      :else (terrastore-error base (extract-body response))
      )
    )
  )

(defn values
  ([base bucket params]
    (let [url (str (strip-slash base) "/" bucket) 
          response (request url "GET" {"Content-Type" "application/json"} {} params)]
      (cond
        (= (response :code) 200) (extract-body response)
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
        response (request url "DELETE")]
    (cond
      (not (= (response :code) 204)) (terrastore-error base (extract-body response))
      )
    )
  )

(defn put-value [base bucket k v]
  (let [url (str (strip-slash base) "/" bucket "/" k)
        response (request url "PUT" {"Content-Type" "application/json"} {} {} v)]
    (cond
      (= (response :code) 204) (extract-body response)
      :else (terrastore-error base (extract-body response))
      )
    )
  )

(defn conditionally-put-value [base bucket k v params]
  (let [url (str (strip-slash base) "/" bucket "/" k)
        response (request url "PUT" {"Content-Type" "application/json"} {} params v)]
    (cond
      (= (response :code) 204) (extract-body response)
      :else (terrastore-error base (extract-body response))
      )
    )
  )

(defn get-value [base bucket k]
  (let [url (str (strip-slash base) "/" bucket "/" k)
        response (request url "GET" {"Content-Type" "application/json"})]
    (cond
      (= (response :code) 200) (extract-body response)
      :else (terrastore-error base (extract-body response))
      )
    )
  )

(defn conditionally-get-value [base bucket k params]
  (let [url (str (strip-slash base) "/" bucket "/" k)
        response (request url "GET" {"Content-Type" "application/json"} {} params)]
    (cond
      (= (response :code) 200) (extract-body response)
      :else (terrastore-error base (extract-body response))
      )
    )
  )

(defn remove-value [base bucket k]
  (let [url (str (strip-slash base) "/" bucket "/" k)
        response (request url "DELETE")]
    (cond
      (not (= (response :code) 204)) (terrastore-error base (extract-body response))
      )
    )
  )

(defn do-export [base bucket params]
  (let [url (str (strip-slash base) "/" bucket "/export")
        response (request url "POST" {} {} params)]
    (cond
      (not (= (response :code) 204)) (terrastore-error base (extract-body response))
      )
    )
  )

(defn do-import [base bucket params]
  (let [url (str (strip-slash base) "/" bucket "/import")
        response (request url "POST" {} {} params)]
    (cond
      (not (= (response :code) 204)) (terrastore-error base (extract-body response))
      )
    )
  )

(defn do-update [base bucket k update params]
  (let [url (str (strip-slash base) "/" bucket "/" k "/update")
        response (request url "POST" {"Content-Type" "application/json"} {} params update)]
    (cond
      (= (response :code) 200) (extract-body response)
      :else (terrastore-error base (extract-body response))
      )
    )
  )

(defn do-predicate-query [base bucket params]
  (let [url (str (strip-slash base) "/" bucket "/predicate")
        response (request url "GET" {"Content-Type" "application/json"} {} params)]
    (cond
      (= (response :code) 200) (extract-body response)
      :else (terrastore-error base (extract-body response))
      )
    )
  )

(defn do-range-query [base bucket params]
  (let [url (str (strip-slash base) "/" bucket "/range")
        response (request url "GET" {"Content-Type" "application/json"} {} params)]
    (cond
      (= (response :code) 200) (extract-body response)
      :else (terrastore-error base (extract-body response))
      )
    )
  )