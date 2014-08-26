(ns thermador.rest
  (:require [cheshire.core :as cheshire :refer [generate-string]]))

(defmulti make-return
  (fn [d]
    (cond
     (vector? d) :many
     (map? @d) :single)))
(defmethod make-return :single
  [pobj]
  (let [pobj-val (@pobj)
        ret-obj (dissoc pobj-val :datum-name :prototype)
        body (generate-string ret-obj)
        status 200
        headers {"Content-Type" "text/html"}]
    {:status status
     :headers headers
     :body body}))
(defmethod make-return :many
  [pobjects]
  (let [ret-objects (into [] (for [pobj pobjects
                                   :let [pobj-val @pobj
                                         ret-obj (dissoc pobj-val :datum-name :prototype)]]
                               ret-obj))
        body (generate-string ret-objects)
        status 200
        headers {"Content-Type" "text/html"}]
    {:status status
     :headers headers
     :body body}))
(defmethod make-return :default
  [pobj]
  {:status 404
   :headers {"Content-Type" "text/html"}})
