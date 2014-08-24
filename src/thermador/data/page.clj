(ns thermador.data.page
  (:require [thermador.data.model :as model]))

(def -PageEntityFields
  {:title ""
   :id 0
   :body ""
   :datum-name ""})

(def -PageModelFields
  {:datum-name "Page"})

(def Page model/create-model "Page" -PageModelFields)

(defn create-page
  ([] (model/create Page -PageEntityFields))
  ([fields] (model/create Page Page -PageEntityFields fields)))
