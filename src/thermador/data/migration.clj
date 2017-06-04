(ns thermador.data.migration
  (:require [clojure.string :as string]
            [me.raynes.fs :as fs]
            [taoensso.timbre :as log]
            [thermador.data.model :as model]
            [thermador.data.migration.page :as page]
            [thermador.data.migration.static-assets :as sa]
            [thermador.config.dropbox :as dbx]))

(def model-migration-map (into {} [page/page-migration]))
(def static-assets-map (merge sa/gif-migration))

(defn make-id-from-file-name
  [file-name]
  (first (string/split file-name #"\.")))

(defn sync-redis
  []
  (log/info "Syncing Redis with Dropbox for known models.")
  (doall
   (for [[sync-model data] model-migration-map
         :let [candidate-pairs (dbx/list-folder-contents (:path data))]]
     (doseq [[file-name path] candidate-pairs
             :let [id (make-id-from-file-name file-name)
                   {:keys [lookup-key create-fn update-fn]} data
                   file-content-stream (dbx/load-file-from-dbx path)]]
       (if (nil? (model/retrieve :lookup-id lookup-key sync-model id))
         (do (log/info "Found new model:" sync-model id)
             (create-fn id file-content-stream))
         (do (log/info "Updating existing model:" sync-model id)
             (update-fn lookup-key sync-model id file-content-stream))))))
  (log/info "Done syncing models"))

(defn -check-and-copy-assets [candidate-pairs asset-map]
  (let [{:keys [create-fn exists-fn]} asset-map]
    (doseq [[file-name path] candidate-pairs]
      (if-not (exists-fn file-name)
        (->> (dbx/load-file-from-dbx path)
             (create-fn file-name))))))

(defn sync-static-assets []
  (log/info "Syncing static assets from Dropbox")
  (doseq [[asset-key asset-map] static-assets-map
          :let [path (:path asset-map)
                candidate-pairs (dbx/list-folder-contents path)]]
    (log/info "Migrating " asset-key)
    (-check-and-copy-assets candidate-pairs asset-map))
  (log/info "Done syncing assets"))
