(ns user
  (:require [nbk.core :refer :all]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre]))

(timbre/refer-timbre)

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


  (go)

  (start)

  (stop)

  (-> system :graph)

  )
