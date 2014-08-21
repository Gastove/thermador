(ns thermador.data.page
  (:require [thermador.data.proto :as proto]
            [thermador.data.base :as base]))

(def -PageDefaults
  {:title ""
   :id 0
   :body ""
   :redis-key-name "Page"})

(def Page
  (let [n (proto/beget base/Base)]
    (proto/extend n -PageDefaults)))
