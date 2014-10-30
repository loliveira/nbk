(ns nbk.http
  (:require [org.httpkit.server :refer [run-server]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [ring.middleware.defaults :refer [site-defaults]]
            [com.stuartsierra.component :as component]
            [nbk.routes :refer [main-routes]]
            [taoensso.timbre :as timbre]))

(timbre/refer-timbre)


(defn wrap-graph-component [handler graph]
  (fn [req]
    (handler (assoc req :graph graph))))



(defrecord HttpServer [port]
  component/Lifecycle
  (start [{server :server graph :graph :as component}]
         (if server
           component
           (do
             (debug "starting http server." )
             (->> (run-server (-> main-routes
                                  wrap-json-response
                                  wrap-json-params
                                  wrap-reload
                                  wrap-stacktrace
                                  (wrap-graph-component graph))
                              {:port port})
                  (assoc component :server)))))
  (stop [{server :server :as component}]
        (if (not server)
          component
          (do
            (debug "stoping http server.")
            (server)
            (assoc component :server nil)))))

(defn new-http-server [{port :port}]
  (map->HttpServer {:port port}))
