(ns thermador.data.model
  "An implementation of a data model. Read and write the model to db; modify
  and create models."
  (:require [thermador.config.database :as datastore]
            [thermador.data.proto :as proto]
            [taoensso.carmine :as carmine]
            [clojure.string :as string])
  (:import [org.joda.time DateTime DateTimeZone]))

(defn now
  "Utility function for current time in millis."
  []
  (DateTime. DateTimeZone/UTC))

(def Base
  "The root for all the models in the server. Provides a
  redis key prefix and a created-on time... which makes no sense."
  ;; TODO: probs. don't want created-on here. Makes no sense.
  {:datum-name "Thermador"
   :created-on (now)})

(defn create-model
  [model-fields & {parent-obj :parent :or {parent-obj Base}}]
  (let [new-model (proto/beget parent-obj)]
    (proto/extend new-model model-fields)))

(declare store-pobj store-key make-key)
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
        pobj-key (make-key :datum-name new-pobj)
        parent-key (make-key :datum-name model)]
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

(defmulti retrieve (fn [dispatch-val & args] dispatch-val))
(defmethod retrieve :all
  [dispatch-val lookup-key model]
  (let [set-key (make-key lookup-key model)
        all-keys (datastore/db (carmine/get set-key))]
    (datastore/db (carmine/mget all-keys))))
(defmethod retrieve :all-like
  [dispatch-val key-pattern]
  (let [like-keys (datastore/db (carmine/keys key-pattern))]
    (datastore/db (carmine/mget like-keys))))
(defmethod retrieve :key
  [dispatch-val k]
  (datastore/db (carmine/get k)))
(defmethod retrieve :lookup-id
  [dispatch-val lookup-key model id]
  (let [k (make-key lookup-key model id)]
    (datastore/db (carmine/get k))))



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
             :else (into acc (reduce woah [] v))))]
    (reduce woah [] pobj)))

(defn make-key
  ([lookup-key pobj]
     (let [key-parts (key-chain lookup-key pobj)]
       (datastore/assemble-redis-key key-parts)))
  ([lookup-key model entity-id]
     (let [model-parts (key-chain lookup-key model)
           entity-key (string/capitalize entity-id)
           key-parts (conj model-parts entity-key)]
       (datastore/assemble-redis-key key-parts))))
