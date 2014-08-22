(ns thermador.data.page
  (:require [thermador.data.proto :as proto]
            [thermador.data.base :as base]))

(def -PageChildFields
  {:title ""
   :id 0
   :body ""
   :redis-key-name ""})

(declare create retrieve)
(def -PageFields
  {:redis-key-name "Page"
   :create
   :retrieve})

(def Page
  (let [n (proto/beget base/Base)]
    (proto/extend n -PageFields)))

(defn create
  ""
  ([])
  ([fields]
     (let [new-pobj (proto/beget Page)
           obj-fields (merge -PageChildFields fields)
           new-pobj (proto/extend new-pobj obj-fields)
           key-parts (proto/key-chain new-pobj)
           pobj-key (datastore/assemble-redis-key key-parts) ;This needs to go elsewhere.
           parent-key-parts (proto/key-chain (:prototype new-pobj))
           parent-key (datastore/assemble-redis-key parent-key-parts)]
       (base/store-pobj new-pobj pobj-key)
       (base/store-key parent-key pobj-key))))

(defn retrieve [])
