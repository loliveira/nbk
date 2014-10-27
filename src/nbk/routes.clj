(ns nbk.routes
  (:use [ring.middleware.json :only (wrap-json-response wrap-json-params)])
  (:require [compojure.core :refer [GET defroutes context]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response]

            [nbk.views.graph.routes]

            ))


(defroutes main-routes
  (context "/graph" [] nbk.views.graph.routes/routes)

  (route/files "/" {:root "resources/public"})
  (route/resources "/")
  (route/not-found "Page not found"))

(def handler
  (-> main-routes
      handler/site))



