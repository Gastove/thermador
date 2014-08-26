(ns thermador.rest
  (:require [cheshire.core :as cheshire :refer [generate-string]]
            [compojure.core :refer [defroutes GET]]
            [thermador.data.model :as model]
            [thermador.data.page :as page]))

(defmulti make-return
  (fn [d]
    (cond
     (vector? d) :many
     (map? @d) :single)))
(defmethod make-return :single
  [pobj]
  (let [pobj-val @pobj
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

(defmulti api-result (fn [resource & args] resource))
(defmethod api-result "page"
  [resource & args]
  (if (empty? args)
    (let [models (model/retrieve :all :datum-name page/Page)]
      (make-return models))
    (case (first args)
            "get" (make-return (model/retrieve :lookup-id (second args))))))

(defroutes rest-routes
  (GET "/:resource/:id" [resource id] (api-result resource "get" id))
  (GET "/:resource/" [resource] (api-result resource)))
