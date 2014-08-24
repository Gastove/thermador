(ns thermador.data.model
  "An implementation of a data model. Read and write the model to db; modify
  and create models."
  (:require [thermador.config.database :as datastore]
            [thermador.data.proto :as proto]
            [taoensso.carmine :as carmine]
            [clojure.string :as string])
  (:import [org.joda.time DateTime DateTimeZone]))

(defn now
  []
  (DateTime. DateTimeZone/UTC))

(def Base
  {:datum-name "Thermador"
   :created-on (now)})

(defn create-model
  [model-fields & {parent-obj :parent :or {parent-obj Base}}]
  (let [new-model (proto/beget parent-obj)]
    (proto/extend new-model model-fields)))

(declare store-pobj store-key key-chain)
(defn create
  [model default-entity-fields entity-fields]
  {:pre [(not (string/blank? (:datum-name entity-fields)))]}
  (let [base (proto/beget model)
        obj-fields (merge default-entity-fields entity-fields)
        new-pobj (proto/extend base obj-fields)
        key-parts (key-chain :datum-name new-pobj)
        pobj-key (datastore/assemble-redis-key key-parts)
        parent-key-parts (key-chain :datum-name model)
        parent-key (datastore/assemble-redis-key parent-key-parts)]
    (store-pobj new-pobj pobj-key)
    (store-key parent-key pobj-key)
    (atom new-pobj)))

(defn transform
  "Applies a function to this proto object.

  There's a lot that should happen to make sure an edit is
  correct; that work will *eventually* happen here. Probably.
  "
  [pobj f]
  (let [key-parts (key-chain :datum-name pobj)
        db-key (datastore/assemble-redis-key key-parts)]
    (swap! pobj f)
    (datastore/db set db-key pobj)))

(defn retrieve
  [k]
  (datastore/db carmine/get k))

(defn store-pobj
  [pobj k]
  (datastore/db carmine/set k pobj))

(defn store-key
  [set-key k]
  (datastore/db carmine/sadd set-key k))

(defn key-chain
  "A key can exist in many levels of a prototype chain.
  Returns a list of all the values for a key in Parent->Child order."
  [lookup-key pobj]
  (letfn [(woah [acc [k v]]
            (cond
             (= k lookup-key) (conj acc v)
             (and (not= k lookup-key) (not= k :prototype)) acc
             :else (into acc (reduce woah [] v))))]
    (reverse (reduce woah [] pobj))))
