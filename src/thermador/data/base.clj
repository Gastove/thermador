(ns thermador.data.base
  (:require [thermador.config.database :as datastore]
            [thermador.data.proto :as proto])
  (:import [org.joda.time DateTime DateTimeZone]))

(defn now
  []
  (DateTime. DateTimeZone/UTC))

(defn transform
  "Applies a function to this proto object.

  There's a lot that should happen to make sure an edit is
  correct; that work will *eventually* happen here.
  "
  [pobj f]
  (let [new-pobj (f pobj)
        key-vector (proto/key-chain new-pobj)
        db-key (datastore/assemble-redis-key key-vector)]
    (datastore/db set db-key new-pobj)))

(def Base
  {:redis-key-name "Thermador"
   :created-on (now)})
