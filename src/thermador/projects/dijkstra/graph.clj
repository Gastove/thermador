(ns thermador.projects.dijkstra.graph
  (require [clojure.core.matrix :as mtx]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Representing a map:
;; Each cell will have one of the following statuses:
;; :v -- visited
;; :u -- un-visited
;; :b -- blocked
;; :c -- current
;; :s -- starting point
;; :g -- goal
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def test-matrix
  [[:s :u :u :u :u]
   [:u :u :u :u :u]
   [:u :u :u :u :u]
   [:u :u :u :u :u]
   [:u :u :u :u :g]])


(defn find-neighbords
  "For a given point p, consisting of a vector [x y],
  list all valid neighbors."
  [m p]
  (let [find-range (fn [a mx] [(max (- a 1) 0) (min (+ a 1) mx)])
        [x y] p
        [rows cols] (mtx/shape m)
        [x-min x-max] (find-range x cols)
        [y-min y-max] (find-range y rows)
        candidates (for [x1 (range x-min (+ 1 x-max))
                         y1 (range y-min (+ 1 y-max))]
                     [x1 y1])]
    (filter #(not= % p) candidates)))

(defn value-of-node
  [m p]
  (apply mtx/mget m p))

(defn get-candidate-values
  [m candidates]
  (map #(value-of-node m %) candidates))

(defn filter-by-val
  [m nodes v]
  (filter #(= (value-of-node m %) v) nodes))

(defn remove-by-val
  [m nodes v]
  (filter #(not= (value-of-node m %) v) nodes))

(defn get-unvisited
  [m nodes]
  (filter-by-val m nodes :u))

(defn remove-start
  [m nodes]
  )
