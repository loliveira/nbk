(ns nbk.graph
  (:require [com.stuartsierra.component :as component]
            [clojure.java.io :refer [reader]]
            [clojure.string :as s]
            [clojure.set :as set]
            [clojure.data.priority-map :refer [priority-map priority-map-keyfn]]

            [taoensso.timbre :as timbre]
            [taoensso.timbre.profiling :as profiling]))

(timbre/refer-timbre)

;(timbre/set-level! :info)






; we use a set to keep the edges. a map could be used
; if the edges have weigth.

(defn- add-edge-impl [g v1 v2]
  (-> g
      (update-in [v1] #(if % % #{}))
      (update-in [v1] conj v2)))


(defn add-edge "Edges are bidirectional"
  [g v1 v2]
  {:pre [(map? g)]}
  (-> g
      (add-edge-impl v1 v2)
      (add-edge-impl v2 v1)))


(defn get-vertices [g]
  (reduce (fn [acc [k v]]
            (-> (conj v k)
                (set/union acc))) #{} g))


(defn shortest-path [prev from to]
  (if (every? prev [from to])
    (loop [cur to
           r (list cur)]
      (debug "shortest-path r - " r)
      (if-not (= cur from)
        (recur (prev cur) (cons (prev cur) r ))
        r))
    '()))

(defn- dijkstra-impl [g from to]
  (loop [queue (priority-map from 0)
         dist {}
         prev {from :no_prev}
         scanned #{}]
    (debug "loop ->>>>> queue - " queue)
    (debug "dist - " dist)
    (debug "prev - " prev)
    (debug "scanned - " scanned)

    (let [[u distance] (peek queue)
          neighbors (g u)
          _ (debug "neighbors - " neighbors)
          outra-coisa (->> neighbors
                           (filter (comp not scanned))
                           (map #(let [alt (inc distance)]
                                   (if (< alt (get dist % Integer/MAX_VALUE))
                                     [% {:distance alt :prev u}])))
                           (filter identity))
          _ (debug "outra-coisa - " outra-coisa)
          new-queue (->> (mapcat (fn [[v {:keys [distance]}]] [v distance]) outra-coisa)
                         (apply priority-map)
                         (into (pop queue)))
          new-dist (merge dist new-queue)
          new-prev (->> (mapcat (fn [[v {:keys [prev]}]] [v prev]) outra-coisa)
                        (apply hash-map)
                        (merge prev))
          ]
      (if (or (empty? new-queue) (prev to))
        prev
        (do
          (debug "new-queue - " new-queue)
          (recur new-queue new-dist new-prev
                   (conj scanned u)))))))

(defn dijkstra [g from to]
  (debug "dijkstra - " from " -> " to)
  (-> (if (every? g [from to])
        (dijkstra-impl g from to)
        {})
      (shortest-path from to)))







(defn read-graph-file [path]
  (with-open [rdr (clojure.java.io/reader path)]
    (reduce conj [] (line-seq rdr))))

(defn parse-line [line]
  (->> (s/split line #"\s")
       (map #(Integer/parseInt %))))

(defn load-graph [path]
  (->> (read-graph-file path)
       (map parse-line)
       (reduce (fn [g [v1 v2]]
                 (add-edge g v1 v2)) {})))

(defn min-val [& args]
  (reduce (fn [[mk mv] [k v]]
            (if (< v mv) [k v] [mk mv]))
          args))


(defn farness-impl
  ([g v] (->> (get-vertices g)
              (map (partial dijkstra g v))
              (map count)
              (apply +)))
  ([g] (->> (get-vertices g)
            (pmap #(vector % (farness-impl g %))))))


(defn closenes-impl [g]
  (->> (farness-impl g)
       (apply min-val)))



(defprotocol GraphApi
  (farness [g] [g v]))





(defrecord Graph []
  component/Lifecycle
  GraphApi
  (start [{graph :graph :as component}]
         (debug "start graph - " component)
         (if graph
           component
           (do
             (debug "starting graph")
             (assoc component :graph (load-graph "resources/edges")))))
  (stop [{graph :graph :as component}]
        (debug "stop graph - " component)
        (if (not graph)
          component
          (do
            (debug "stoping graph")
            (assoc component :graph nil))))
  (farness [{g :graph}]
       (farness-impl g))


  )

(defn new-graph []
  (map->Graph {}))



(comment

  (-> (load-graph "resources/edges")
      (dijkstra 94 11))

  (->> (load-graph "resources/edges")
       farness
       (sort-by second))

  (closenes (load-graph "resources/edges"))

  )







