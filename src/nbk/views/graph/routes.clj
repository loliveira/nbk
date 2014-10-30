(ns nbk.views.graph.routes
  (:require [compojure.core :refer (defroutes GET POST PUT)]
            [nbk.views.graph.index :refer [get-graph add-edge closest load-from-file reset]]
            [taoensso.timbre :as timbre]
            [midje.sweet :refer [=> fact facts]]))

(timbre/refer-timbre)

(defroutes routes
  (GET "/" {graph :graph}
       (get-graph graph))

  (POST "/" [path :as {graph :graph}]
       (load-from-file graph path))

  (GET "/closest" {graph :graph}
       (closest graph))

  (PUT "/edges" [v1 v2 :as {graph :graph}]
        (add-edge graph v1 v2))

  (PUT "/reset" {graph :graph}
       (reset graph))
  )

