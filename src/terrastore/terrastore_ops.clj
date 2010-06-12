(ns terrastore.terrastore-ops (:use clojure.contrib.json.read clojure-http.client))

(defn- strip-slash [base]
  (loop [url base]
    (if (= (- (.length url) 1) (.lastIndexOf url "/"))
      (recur (.substring url 0 (- (.length url) 1)))
      url
      )
    )
  )

(defn- terrastore-error [base error]
  (if (seq error)
    (
      (def error-map (read-json error))
      ((throw (RuntimeException. (str "Message: " (error-map "message") " - Code: " (error-map "code")))))
      )
    )
  )

(defn buckets [base]
  (let [response (request (strip-slash base) "GET")]
    (cond
      (= (:code response) 200) (apply str (:body-seq response))
      :else (terrastore-error base (apply str (:body-seq response)))
      )
    )
  )

(defn values [base bucket]
  (let [url (str (strip-slash base) "/" bucket)
        response (request url "GET" {"Content-Type" "application/json"})]
    (cond
      (= (response :code) 200) (apply str (response :body-seq))
      :else (terrastore-error base (apply str (:body-seq response)))
      )
    )
  )

(defn remove-bucket [base bucket]
  (let [url (str (strip-slash base) "/" bucket)
    response (request url "DELETE")]
    (cond
      (not (= (:code response) 204)) (terrastore-error base (apply str (:body-seq response)))
      )
    )
  )

(defn put-value [base bucket k v]
  (let [url (str (strip-slash base) "/" bucket "/" k)
    response (request url "PUT" {"Content-Type" "application/json"} {} {} v)]
    (cond
      (= (:code response) 204) (apply str (:body-seq response))
      :else (terrastore-error base (apply str (:body-seq response)))
      )
    )
  )

(defn conditionally-put-value [base bucket k v params]
  (let [url (str (strip-slash base) "/" bucket "/" k)
    response (request url "PUT" {"Content-Type" "application/json"} {} params v)]
    (cond
      (= (:code response) 204) (apply str (:body-seq response))
      :else (terrastore-error base (apply str (:body-seq response)))
      )
    )
  )

(defn get-value [base bucket k]
  (let [url (str (strip-slash base) "/" bucket "/" k)
    response (request url "GET" {"Content-Type" "application/json"})]
    (cond
      (= (response :code) 200) (apply str (response :body-seq))
      :else (terrastore-error base (apply str (:body-seq response)))
      )
    )
  )

(defn conditionally-get-value [base bucket k params]
  (let [url (str (strip-slash base) "/" bucket "/" k)
    response (request url "GET" {"Content-Type" "application/json"} {} params)]
    (cond
      (= (response :code) 200) (apply str (response :body-seq))
      :else (terrastore-error base (apply str (:body-seq response)))
      )
    )
  )

(defn remove-value [base bucket k]
  (let [url (str (strip-slash base) "/" bucket "/" k)
    response (request url "DELETE")]
    (cond
      (not (= (:code response) 204)) (terrastore-error base (apply str (:body-seq response)))
      )
    )
  )

(defn do-export [base bucket params]
  (let [url (str (strip-slash base) "/" bucket "/export")
    response (request url "POST" {} {} params)]
    (cond
      (not (= (:code response) 204)) (terrastore-error base (apply str (:body-seq response)))
      )
    )
  )

(defn do-import [base bucket params]
  (let [url (str (strip-slash base) "/" bucket "/import")
    response (request url "POST" {} {} params)]
    (cond
      (not (= (response :code) 204)) (terrastore-error base (apply str (:body-seq response)))
      )
    )
  )

(defn do-update [base bucket k update params]
  (let [url (str (strip-slash base) "/" bucket "/" k "/update")
    response (request url "POST" {"Content-Type" "application/json"} {} params update)]
    (cond
      (= (response :code) 200) (apply str (response :body-seq))
      :else (terrastore-error base (apply str (:body-seq response)))
      )
    )
  )

(defn do-predicate-query [base bucket params]
  (let [url (str (strip-slash base) "/" bucket "/predicate")
    response (request url "GET" {"Content-Type" "application/json"} {} params)]
    (cond
      (= (response :code) 200) (apply str (response :body-seq))
      :else (terrastore-error base (apply str (:body-seq response)))
      )
    )
  )

(defn do-range-query [base bucket params]
  (let [url (str (strip-slash base) "/" bucket "/range")
    response (request url "GET" {"Content-Type" "application/json"} {} params)]
    (cond
      (= (response :code) 200) (apply str (response :body-seq))
      :else (terrastore-error base (apply str (:body-seq response)))
      )
    )
  )