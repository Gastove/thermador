(ns thermador.projects.dykstra
  (require [clojure.core.matrix :as mtx]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Representing a map:
;; Each cell will have one of the following statuses:
;; :v -- visited
;; :u -- unvisited
;; :b -- blocked
;; :c -- current
;; :s -- starting point
;; :g -- goal
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def test-matrix
  [[:s :e :e :e :e]
   [:e :e :e :e :e]
   [:e :e :e :e :e]
   [:e :e :e :e :e]
   [:e :e :e :e :g]])


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

(defn value-of-point
  [m p]
  (apply mtx/mget m p))
