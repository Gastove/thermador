(ns thermador.data.model
  "An implementation of a data model. Read and write the model to db; modify
  and create models."
  (:require [clojure.string :as string]
            [taoensso.carmine :as carmine]
            [taoensso.timbre :as log]
            [thermador.config.database :as datastore]
            [thermador.data.proto :as proto])
  (:import (org.joda.time DateTime DateTimeZone)))

(defn now
  "Utility function for current time in millis."
  []
  (DateTime. DateTimeZone/UTC))

(def Base
  "The root for all the models in the server. Provides a
  redis key prefix and a created-on time... which makes no sense."
  ;; TODO: probs. don't want created-on here. Makes no sense.
  {:datum-name "thermador"
   :created-on (now)
   :lookup-key :datum-name})

(defn create-model
  [model-fields & {parent-obj :parent :or {parent-obj Base}}]
  (let [new-model (proto/beget parent-obj)]
    (proto/extend new-model model-fields)))

(declare store-pobj store-key make-key key-chain)
(defn create
  "Create a new object that is new link in the prototype chain
  off of some existing model. Beget, then extend; set fields.
  Finally, store the key of the child in the keyset of the model,
  store the child, and return the child wrapped in an atom."
  [model default-entity-fields entity-fields]
  {:pre [(not (string/blank? (:datum-name entity-fields)))]}
  (let [base (proto/beget model)
        obj-fields (merge default-entity-fields entity-fields)
        new-pobj (proto/extend base obj-fields)
        pobj-key (make-key new-pobj)
        parent-key (make-key model)]
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
    (datastore/db (carmine/set db-key pobj))))

;; this is.... getting big
;; TODO: Stop passing models. The "model" is the prototype of a pobj
(defmulti retrieve (fn [dispatch-val & args] dispatch-val))
(defmethod retrieve :all
  [_ lookup-key model]
  (let [set-key (make-key model)
        all-keys (datastore/db (carmine/smembers set-key))
        models (datastore/db (apply carmine/mget all-keys))]
    (log/info "Retrieving all models from" set-key)
    (if models
      (do
        (log/debug "Found:" all-keys)
        (into [] (map atom models)))
     (do
       (log/debug "Nothing found in" set-key)
       nil))))
(defmethod retrieve :key
  [_ k]
  (log/info "Retrieving:" k)
  (if-let [datum (datastore/db (carmine/get k))]
    (atom datum)
    nil))
(defmethod retrieve :keys
  [_ ks]
  (log/info "Retrieving:" ks)
  (for [k ks] (retrieve :key k)))
(defmethod retrieve :lookup-id
  [_ lookup-key model id]
  (log/info "Looking for: " id " in" (make-key model))
  (let [k (make-key model id)]
    (if-let [datum (datastore/db (carmine/get k))]
      (atom datum)
      (do
        (log/info id "not found.")
        nil))))

(defn delete
  ([pobjs]
     (into [] (for [pobj pobjs]
                (delete pobj :datum-name))))
  ([pobj lookup-key]                    ; WUGH clean this up jeezus.
     (let [raw-pobj @pobj
           pobj-model (:prototype raw-pobj)
           pobj-key (make-key raw-pobj)
           pobj-model-key (make-key pobj-model)]
       (if (and
            (datastore/db (carmine/del pobj-key))
            (datastore/db (carmine/srem pobj-model-key pobj-key)))
         true
         false))))

(defn store-pobj
  [pobj k]
  (datastore/db (carmine/set k pobj)))

(defn store-key
  "Puts a proto objects key in a set, itself stored at the key
  of the model. I.E., Thermador:Page:HomePage will live in the
  set Thermador:Page"
  [set-key k]
  (datastore/db (carmine/sadd set-key k)))

(defn key-chain
  "A key can exist in many levels of a prototype chain.
  Returns a list of all the values for a key in Parent->Child order."
  [lookup-key pobj]
  (letfn [(woah [acc [k v]]
            (cond
             (= k lookup-key) (conj acc v)
             (and (not= k lookup-key) (not= k :prototype)) acc
             :else (conj acc (reduce woah [] v))))]
    (reduce woah [] pobj)))

(defn flatten-nested-vector
  [nested-vec]
  (let [[a b] nested-vec]
    (if (and a b)
      (if (vector? a)
        (conj (flatten-nested-vector a) b)
        (conj (flatten-nested-vector b) a))
      (if a [a] [b]))))

(defn make-key
  ([pobj]
     (let [model (:prototype pobj)
           key-parts (key-chain :datum-name pobj)
           unpacked-parts (flatten-nested-vector key-parts)]
       (datastore/assemble-redis-key unpacked-parts)))
  ([pobj id]
     (let [key-parts-base (key-chain :datum-name pobj)
           unpacked-parts (flatten-nested-vector key-parts-base)
           key-parts (conj unpacked-parts id)]
       (datastore/assemble-redis-key key-parts)))
  ([lookup-key model id]
     "Tryin real hard to depricate this one"
     (make-key model id)))
