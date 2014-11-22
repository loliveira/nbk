(ns nbk.graph
  (:require [com.stuartsierra.component :as component]
            [clojure.java.io :refer [reader]]
            [clojure.string :as s]
            [clojure.set :as set]
            [clojure.data.priority-map :refer [priority-map priority-map-keyfn]]

            [taoensso.timbre :as timbre]
            [taoensso.timbre.profiling :as profiling]))

(timbre/refer-timbre)


(defn- connect-vertices-old [g v1 v2]
  (-> g
      (update-in [v1] #(if % % #{}))
      (update-in [v1] conj v2)))

(defn- connect-vertices [g v1 v2]
  (update-in g [v1] (fnil #(conj % v2) #{})))



(defn add-edge-impl [g v1 v2]
  {:pre [(map? g)]}
  (-> g
      (connect-vertices v1 v2)
      (connect-vertices v2 v1)))


(defn get-vertices [g]
  (keys g))


(defn- shortest-path [prev from to]
  (if (every? prev [from to])
    (loop [cur to
           r (list cur)]
      (debug "shortest-path r - " r)
      (if-not (= cur from)
        (recur (prev cur) (cons (prev cur) r ))
        r))
    '()))


(defn dijkstra-impl [g from to]
  (loop [queue (priority-map from 0)
         dist {from 0}
         scanned #{}]
    (debug "loop ->>>>> queue - " queue)
    (debug "dist - " dist)
    (debug "scanned - " scanned)

    (let [[u distance] (peek queue)
          neighbors (g u)
          _ (trace "neighbors - " neighbors)
          unvisited (->> neighbors
                           (filter (comp not scanned))
                           (map #(let [alt (inc distance)]
                                   (if (< alt (get dist % Integer/MAX_VALUE))
                                     [% {:distance alt}])))
                           (filter identity))
          _ (trace "unvisited - " unvisited)
          new-queue (->> (mapcat (fn [[v {:keys [distance]}]] [v distance]) unvisited)
                         (apply priority-map)
                         (into (pop queue)))
          new-dist (merge dist new-queue)]
      (if (or (empty? new-queue) (new-dist to))
        (new-dist to)
        (do
          (debug "new-queue - " new-queue)
          (recur new-queue new-dist
                 (conj scanned u)))))))

(defn dijkstra [g from to]
  (debug "dijkstra - " from " -> " to)
  (if (every? g [from to])
    (dijkstra-impl g from to)))





(defn min-val [& args]
  (reduce (fn [[mk mv] [k v]]
            (if (< v mv) [k v] [mk mv]))
          args))


(defn farness-impl
  ([g v] (->> (get-vertices g)
              (map (partial dijkstra g v))
              (apply +)))
  ([g] (->> (get-vertices g)
            (pmap #(vector % (farness-impl g %))))))

(defn central-vertex [g]
  (->> (farness-impl g)
       (apply min-val)
       first))


(defn read-graph-file [path]
  (with-open [rdr (clojure.java.io/reader path)]
    (reduce conj [] (line-seq rdr))))

(defn parse-line [line]
  (->> (s/split line #"\s")
       (map #(Integer/parseInt %))))

(defn load-graph-impl [path]
  (->> (read-graph-file path)
       (map parse-line)
       (reduce (fn [g [v1 v2]]
                 (add-edge-impl g v1 v2)) {})))


(defprotocol GraphApi
  (load-from-file [component path])
  (add-edge [component v1 v2])
  (reset [component])
  (closest [component])
  )


(defrecord Graph []
  component/Lifecycle
  GraphApi
  clojure.lang.IDeref

  (start [component] component)

  (stop [{g :g :as component}]
        (if (not g)
          component
    (assoc component :g nil)))

  (load-from-file [{g :g} path]
    (reset! g (load-graph-impl path)))

  (add-edge [{g :g} v1 v2]
    (swap! g add-edge-impl v1 v2))

  (reset [{g :g}]
    (reset! g {}))

  (closest [{g :g}]
    (central-vertex @g))

  (deref [{g :g}] @g))


(defn new-graph
  "constructor"
  []
  (map->Graph {:g (atom {})}))







