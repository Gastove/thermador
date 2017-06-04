(ns thermador.config.init
  (:require [thermador.data.migration :as migration]
            [thermador.config.logging-config :as log]))

(defn init!
  []
  (log/configure-logging)
  (migration/sync-redis)
  (migration/sync-static-assets))
