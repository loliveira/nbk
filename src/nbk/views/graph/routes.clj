(ns nbk.views.graph.routes
  (:require [compojure.core :refer (defroutes GET)]
            [nbk.views.graph.index :refer [index-json]]
            [taoensso.timbre :as timbre]))

(timbre/refer-timbre)

(defroutes routes
  (GET "/" {g :graph}
      (index-json g)))

