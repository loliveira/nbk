(ns nbk.http
  (:require [org.httpkit.server :refer [run-server]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [com.stuartsierra.component :as component]
            [nbk.routes :refer [main-routes]]
            ;[nbk.graph :as graph]
            [taoensso.timbre :as timbre]))

(timbre/refer-timbre)

;(timbre/set-level! :info)


(defn wrap-graph-component [handler graph]
  (fn [req]
    (handler (assoc req :graph graph))))



(defrecord HttpServer []
  component/Lifecycle
  (start [{server :server graph :graph :as component}]
         (if server
           component
           (do
             (debug "starting http server." )
             (->> (run-server (-> main-routes
                                  wrap-reload
                                  wrap-stacktrace
                                  (wrap-graph-component graph))
                              {:port 4000})
                  (assoc component :server)))))
  (stop [{server :server :as component}]
        (if (not server)
          component
          (do
            (debug "stoping http server.")
            (server)
            (assoc component :server nil)))))

(defn new-http-server [args]
  (map->HttpServer args))
