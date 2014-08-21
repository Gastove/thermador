(ns thermador.data.base
  (:import [org.joda.time DateTime DateTimeZone]))

(defn now
  []
  (DateTime. DateTimeZone/UTC))

(def Base
  {:redis-key-name "Thermador"
   :created-on (now)})
