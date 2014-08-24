(ns thermador.data.page
  (:require [thermador.data.model :as model]))

(def -PageEntityFields
  {:title ""
   :id 0
   :body ""
   :datum-name ""})

(def -PageModelFields
  {:datum-name "Page"})

(def Page (model/create-model -PageModelFields))

(defn create-page
  [fields]
  (model/create Page -PageEntityFields fields))
