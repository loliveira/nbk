(ns nbk.views.graph.index
  (:require [compojure.core :refer (defroutes GET)]
            [net.cgrand.enlive-html :as enlive]
            [net.cgrand.reload :refer [auto-reload]]
            [cheshire.core :as json]
            [nbk.graph :as graph]
            ))


(auto-reload *ns*)

(enlive/deftemplate index "nbk/views/graph/index.html"
  []
  [:#graph-script]  (->> (json/generate-string {:a 2})
                         (hash-map :json)
                         enlive/replace-vars
                         enlive/transform-content)
  )



(defn index-json [g]
  (json/generate-string (.farness g))
  )















