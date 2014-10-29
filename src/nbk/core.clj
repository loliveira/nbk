(ns nbk.core
  (:require [com.stuartsierra.component :as component]

            [nbk.graph :as graph]
            [nbk.http :as http-server]

            [taoensso.timbre :as timbre]))

(timbre/refer-timbre)

(timbre/set-level! :info)


(defn nbk-system [{port :port}]
  (component/system-map
   :graph (graph/new-graph)
   :app (component/using
          (http-server/new-http-server {:port port})
          [:graph])))

