(ns thermador.data.base
  (:require [thermador.config.database :as datastore]
            [thermador.data.proto :as proto]
            [taoensso.carmine :as carmine])
  (:import [org.joda.time DateTime DateTimeZone]))

(defn now
  []
  (DateTime. DateTimeZone/UTC))

(defn transform
  "Applies a function to this proto object.

  There's a lot that should happen to make sure an edit is
  correct; that work will *eventually* happen here. Probably.
  "
  [pobj f]
  (let [new-pobj (f pobj)
        key-vector (proto/key-chain new-pobj)
        db-key (datastore/assemble-redis-key key-vector)]
    (datastore/db set db-key new-pobj)))

(defn store-pobj
  [pobj k]
  (datastore/db carmine/set object-key pobj))

(defn store-key
  [set-key k]
  (datastore/db carmine/sadd set-key k))

(defn retrieve-pobj
  [redis-key]
  (datastore/db carmine/get redis-key))

(def Base
  {:redis-key-name "Thermador"
   :created-on (now)})
