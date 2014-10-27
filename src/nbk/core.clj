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



(def system nil)


(defn init []
  (alter-var-root #'system
    (constantly (nbk-system {:port 4000}))))

(defn start []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system
    (fn [s] (when s (component/stop s)))))

(defn go []
  (init)
  (start)
  )















(comment

  (init)
  (start)

  (stop)




  (defn reset []
    (stop)
    (refresh :after 'user/go))
  )
