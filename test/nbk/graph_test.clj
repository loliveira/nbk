(ns nbk.graph-test
  (:require [midje.sweet :refer [=> fact facts]]
            [nbk.graph :refer :all]))


(facts "about Graph..."
  (fact "adding one edge" (add-edge {} 1 2) => {1 #{2} 2 #{1}})
  (fact "adding the same edge twice, but iverting the vertices order"
    (-> {}
        (add-edge 1 2)
        (add-edge 2 1)) => {1 #{2} 2 #{1}}))




(facts "about dijkstra"
  (fact "empty graph"
    (let [g {}]
      (dijkstra g 42 42) => '()))

  (fact "just one vertex, valid from and to, no edges"
    (let [g {1 #{}}]
      (dijkstra g 1 1) => '(1)))

  (fact "just one vertex, invalid from, no edges"
    (let [g {1 #{}}]
      (dijkstra g 42 42) => '()))

  (fact "just one vertex, valid from, no edges"
    (let [g {1 #{}}]
      (dijkstra g 1 42) => '()))

  (fact "two disconnected vertices"
    (let [g {1 #{} 2 #{}}]
      (dijkstra g 1 2) => '()))

  (fact "two connected vertices"
    (let [g (-> {} (add-edge 1 2))]
      (dijkstra g 1 2) => '(1 2)
      (dijkstra g 2 1) => '(2 1)))

  (fact "six vertices, two disconnected groups"
    (let [g (-> {}
                (add-edge 1 2)
                (add-edge 2 3)
                (add-edge 4 5)
                (add-edge 5 6))]
      (dijkstra g 1 3) => '(1 2 3)
      (dijkstra g 4 6) => '(4 5 6)

      (dijkstra g 1 6) => '()))

  (fact "four vertices, same distance from 1 to 4"
    (let [g (-> {}
                (add-edge 1 2)
                (add-edge 1 3)
                (add-edge 2 4)
                (add-edge 3 4)
                )]
      (dijkstra g 1 4) => '(1 3 4)))

  (fact "six vertices, one shortcut from 4 to 6"
    (let [g (-> {}
                (add-edge 1 2)
                (add-edge 2 3)
                (add-edge 3 4)
                (add-edge 4 5)
                (add-edge 5 6)
                (add-edge 4 6) ; <- shortcut
                )]
      (dijkstra g 1 6) => '(1 2 3 4 6)
      (dijkstra g 6 1) => '(6 4 3 2 1)))
)
