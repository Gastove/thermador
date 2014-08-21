(ns thermador.data.base
  (:import [org.joda.time DateTime DateTimeZone]))

(defn now
  []
  (DateTime. DateTimeZone/UTC))

(def Base
  {:created-on (now)})
