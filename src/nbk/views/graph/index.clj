(ns nbk.views.graph.index
  (:require [compojure.core :refer (defroutes GET)]
            [taoensso.timbre :as timbre]
            [ring.util.response :refer [response]]))

(timbre/refer-timbre)


(defn get-graph [g]
  (response @g))

(defn add-edge [g v1 v2]
  (response (.add-edge g v1 v2)))

(defn closest [g]
  (response {:closest (.closest g)}))

(defn load-from-file [g path]
  (response (.load-from-file g path)))

(defn reset [g]
  (response (.reset g)))









