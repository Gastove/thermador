(ns thermador.data.migration
  (:require [clojure.string :as string]
            [taoensso.timbre :as log]
            [thermador.data.model :as model]
            [thermador.data.migration.page :as page]
            [thermador.config.dropbox :as dbx]))

(def sync-map (into {} [page/page-migration]))

(defn make-id-from-file-name
  [file-name]
  (first (string/split file-name #"\.")))

(defn sync-redis
  []
  (log/info "Syncing Redis with Dropbox for known models.")
  (doall
   (for [[sync-model data] sync-map
         :let [candidate-pairs (dbx/list-files-in-folder (:path data))]]
     (doseq [[file-name path] candidate-pairs
             :let [id (make-id-from-file-name file-name)
                   {:keys [lookup-key create-fn update-fn]} data
                   markdown (dbx/load-file-from-dbx path)]]
       (if (nil? (model/retrieve :lookup-id lookup-key sync-model id))
         (do (log/info "Found new model:" sync-model id)
             (create-fn id markdown))
         (do (log/info "Updating existing model:" sync-model id)
             (update-fn lookup-key sync-model id markdown)))))))
