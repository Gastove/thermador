(ns thermador.config.logging-config
  (:require [taoensso.timbre :as log]))

(def logging-config
  {:timestamp-pattern "yyyy-MM-dd HH:mm:ss ZZ"})

(defn configure-logging
  []
  (doseq [[k v ] logging-config]
    (log/set-config! [k] v))
  (log/info "Logging configs set."))
