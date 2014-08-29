(ns thermador.rest
  (:require [cheshire.core :as cheshire :refer [generate-string]]
            [compojure.core :refer [defroutes GET]]
            [thermador.data.model :as model]
            [thermador.data.page :as page]))

(defmulti make-return
  (fn [d]
    (cond
     (vector? d) :many
     (map? d) :single)))
(defmethod make-return :single
  [ret-obj]
  (let [body (generate-string ret-obj)
        status 200
        headers {"Content-Type" "text/html"}]
    {:status status
     :headers headers
     :body body}))
(defmethod make-return :many
  [ret-objects]
  (let [body (generate-string ret-objects)
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
    (let [models (model/retrieve :all :datum-name page/Page)
          returnable-models (into [] (map page/make-rest-return models))]
      (make-return returnable-models))
    (case (first args)
      "get" (let [id (second args)
                  pobj (model/retrieve :lookup-id :datum-name page/Page id)
                  ret-obj (page/make-rest-return pobj)]
                (make-return ret-obj)))))

(defroutes rest-routes
  (GET "/:resource/:id" [resource id] (api-result resource "get" id))
  (GET "/:resource" [resource] (api-result resource)))
