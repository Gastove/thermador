(ns thermador.data.page
  (:require [thermador.data.model :as model]
            [compojure.core :refer [defroutes GET]]
            [cheshire.core :refer [generate-string]])
  (:import [org.joda.time.format DateTimeFormat]))

(def -PageEntityFields
  {:title ""
   :body ""
   :name ""
   :datum-name ""
   :created-on (model/now)})

(def -PageModelFields
  {:datum-name "page"})

(def Page (model/create-model -PageModelFields))

(def lookup-key :datum-name)

(defn create-page
  ([fields]
     (model/create Page -PageEntityFields fields))
  ([name title body & {:keys [datum-name] :or {datum-name name}}]
     (let [fields {:name name
                   :title title
                   :body body
                   :datum-name datum-name}]
       (create-page fields))))

(defn format-datetime
  [dt]
  (let [formatter (DateTimeFormat/forPattern "yyyy-MM-dd HH:mm:ss")]
    (.print formatter dt)))

(defn make-rest-return
  [pobj]
  (let [data @pobj
        created-on (format-datetime (:created-on data))
        ret-obj (select-keys data [:title :body :name])
        ret-obj (assoc ret-obj :created-on created-on)]
    ret-obj))

;; (defroutes page-routes
;;   (GET "/:id" [id] (rest-api/make-return (model/retrieve :lookup-id :datum-name Page id)))
;;   (GET "/" [] (rest-api/make-return (model/retrieve :all :datum-name Page))))
