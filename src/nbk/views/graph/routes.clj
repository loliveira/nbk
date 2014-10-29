(ns nbk.views.graph.routes
  (:require [compojure.core :refer (defroutes GET POST PUT)]
            [nbk.views.graph.index :refer [index-html get-json]]
            [cheshire.core :as json]
            [taoensso.timbre :as timbre]
            [midje.sweet :refer [=> fact facts]]))

(timbre/refer-timbre)

(defroutes routes
  (GET "/" {graph :graph}
       (let [g (-> graph :g)]
         (get-json @g)))

  (POST "/" {body :body}
        (info (slurp body))

        ""
        )

  (PUT "/" [body]
       (info "req - " (slurp body))
       ".")
  )

