(ns thermador.config.database
  (:require [environ.core :refer [env]]
            [taoensso.carmine :as carmine :refer [wcar]]))

(def DB (or (env :database-url) "postgres://localhost:5432/thermador"))

(def conn-props {:pool {} :spec {:host "localhost" :port 6379}})

(defmacro db
  "Do something in the context of the configured DB"
  [& body]
  `(wcar conn-props ~@body))

(defn assemble-redis-key
  "Makes a Redis key from a vector of strings"
  [strs]
  (apply str (interpose ":" strs)))
