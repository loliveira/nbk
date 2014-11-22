(ns nbk.graph-test
  (:require [com.stuartsierra.component :as component]
            [midje.sweet :refer [=>  throws fact facts]]
            [nbk.core :as core]
            [nbk.graph :refer :all]
            [nbk.rest-client :refer [do-post do-get do-put]])
  (:import [java.io FileNotFoundException]))


(facts "about Graph..."
       (fact "adding one edge" (add-edge-impl {} 1 2) => {1 #{2} 2 #{1}})
       (fact "adding the same edge twice, but iverting the vertices order"
             (-> {}
                 (add-edge-impl 1 2)
                 (add-edge-impl 2 1)) => {1 #{2} 2 #{1}}))


(facts "about dijkstra"
       (fact "empty graph"
             (let [g {}]
               (dijkstra g 42 42) => nil))

       (fact "just one vertex, valid from and to, no edges"
             (let [g {1 #{}}]
               (dijkstra g 1 1) => 0))

       (fact "just one vertex, invalid from, no edges"
             (let [g {1 #{}}]
               (dijkstra g 42 42) => nil))

       (fact "just one vertex, valid from, no edges"
             (let [g {1 #{}}]
               (dijkstra g 1 42) => nil))

       (fact "two disconnected vertices"
             (let [g {1 #{} 2 #{}}]
               (dijkstra g 1 2) => nil))

       (fact "two connected vertices"
             (let [g (-> {} (add-edge-impl 1 2))]
               (dijkstra g 1 2) => 1
               (dijkstra g 2 1) => 1))

       (fact "six vertices, two disconnected groups"
             (let [g (-> {}
                         (add-edge-impl 1 2)
                         (add-edge-impl 2 3)
                         (add-edge-impl 4 5)
                         (add-edge-impl 5 6))]
               (dijkstra g 1 3) => 2
               (dijkstra g 4 6) => 2

               (dijkstra g 1 6) => nil))

       (fact "four vertices, same distance from 1 to 4"
             (let [g (-> {}
                         (add-edge-impl 1 2)
                         (add-edge-impl 1 3)
                         (add-edge-impl 2 4)
                         (add-edge-impl 3 4)
                         )]
               (dijkstra g 1 4) => 2))

       (fact "six vertices, one shortcut from 4 to 6"
             (let [g (-> {}
                         (add-edge-impl 1 2)
                         (add-edge-impl 2 3)
                         (add-edge-impl 3 4)
                         (add-edge-impl 4 5)
                         (add-edge-impl 5 6)
                         (add-edge-impl 4 6) ; <- shortcut
                         )]
               (dijkstra g 1 6) => 4
               (dijkstra g 6 1) => 4))
       )


(facts "about the api"
       (fact  "Graph is a map"
              (let [graph (new-graph)]
                (.load-from-file graph "resources/edges") => map?))

       (fact  "Throws if not found"
              (let [graph (new-graph)]
                (.load-from-file graph "not_found.txt") => (throws  FileNotFoundException)))

       (fact "Finding the central vertex"
             (let [graph (new-graph)]
               (.add-edge graph 1 2)
               (.add-edge graph 2 3)

               (.closest graph) => 2

               (.add-edge graph 3 4)
               (.add-edge graph 3 5)

               (.closest graph) => 3))
       )


(facts "about the rest api"
  (let [system (component/start-system
                (core/nbk-system {:port 4000}))]
    (try

      (fact "PUT /graph/reset sets a new graph"
            (do-put  "http://localhost:4000/graph/reset" nil) => {})

      (fact "GET /graph returns the current graph"
            (do-get "http://localhost:4000/graph") => {})

      (fact "PUT /graph/edges to add a new edge"
            (do-put "http://localhost:4000/graph/edges" {:v1 3 :v2 2}) => {:2 [3] :3 [2]}
            (do-put "http://localhost:4000/graph/edges" {:v1 1 :v2 2}) => {:1 [2] :2 [1 3] :3 [2]})

      (fact "GET /graph returns the current graph"
            (do-get "http://localhost:4000/graph") =>  {:1 [2] :2 [1 3] :3 [2]})

      (fact "GET /graph/closest to get the closest vertex"
            (do-get "http://localhost:4000/graph/closest") => {:closest 2})

      (fact "POST /graph to load a graph from disk"
            (do-post "http://localhost:4000/graph" {:path "resources/edges"}) => map?)

      (finally
       (component/stop system)))))
