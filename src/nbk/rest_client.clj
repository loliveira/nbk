(ns nbk.rest-client
  (:require [clj-http.client :as http]))

(defn do-get [endpoint]
  (-> (http/get endpoint {:accept :json
                         :as :json})
    :body))

(defn do-put [endpoint data]
  (-> (http/put endpoint {:accept :json
                          :content-type :json
                          :form-params data
                          :as :json})
      :body))

(defn do-post [endpoint data]
  (-> (http/post endpoint {:accept :json
                          :content-type :json
                          :form-params data
                          :as :json})
      :body))
