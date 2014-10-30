(ns nbk.views.graph.index
  (:require [compojure.core :refer (defroutes GET)]
            [net.cgrand.enlive-html :as enlive]
            [net.cgrand.reload :refer [auto-reload]]
            [taoensso.timbre :as timbre]
            [ring.util.response :refer [response]]))

(timbre/refer-timbre)

(auto-reload *ns*)

(comment
(enlive/deftemplate index-html "nbk/views/graph/index.html"
  [g]
  [:body :code]  (->> (json/generate-string g)
                      (hash-map :json)
                      enlive/replace-vars
                      enlive/transform-content))
  )

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









