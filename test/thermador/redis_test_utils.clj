(ns thermador.redis-test-utils
  (:require [taoensso.carmine :as carmine]
            [thermador.config.database :as datastore]))

(defn add-test-data
  [k v]
  (datastore/db (carmine/set k v)))

(defn cleanup-test-data
  []
  (let [redis-keys (datastore/db (carmine/keys "*thermador-test*"))]
    (doseq [k redis-keys]
      (datastore/db (carmine/del k))
      (datastore/db (carmine/srem "thermador:page" k)))))
