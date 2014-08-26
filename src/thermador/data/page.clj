(ns thermador.data.page
  (:require [thermador.data.model :as model]
            [compojure.core :refer [defroutes GET]]
            [cheshire.core :refer [generate-string]]))

(def -PageEntityFields
  {:title ""
   ;;   :id 0
   :body ""
   :name ""
   :datum-name ""
   :created-on (model/now)})

(def -PageModelFields
  {:datum-name "Page"})

(def Page (model/create-model -PageModelFields))

(defn create-page
  [fields]
  (model/create Page -PageEntityFields fields))

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

(defroutes routes
  (GET "/:id" [id] (make-return (model/retrieve :lookup-id :datum-name Page id)))
  (GET "/" [] (make-return (model/retrieve :all :datum-name Page))))
