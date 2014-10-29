(ns nbk.views.graph.index
  (:require [compojure.core :refer (defroutes GET)]
            [net.cgrand.enlive-html :as enlive]
            [net.cgrand.reload :refer [auto-reload]]
            [cheshire.core :as json]))


(auto-reload *ns*)

(enlive/deftemplate index-html "nbk/views/graph/index.html"
  [g]
  [:body :code]  (->> (json/generate-string g)
                      (hash-map :json)
                      enlive/replace-vars
                      enlive/transform-content))

(defn get-json [g]
  (json/generate-string g))


(defn add-edge [g v1 v2]
  (.add-edge g v1 v2))










