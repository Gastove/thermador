(ns thermador.projects.dijkstra.algorithm
  (require [clojure.core.matrix :as mtx]
           [thermador.projects.dijkstra.graph :as graph]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;;
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn dijkstra
  [g]
  (let [all-nodes (mtx/eseq g)
        candidates (graph/remove-by-val g all-nodes :s)
        distances (zipmap candidates Double/POSITIVE_INFINITY)]))
