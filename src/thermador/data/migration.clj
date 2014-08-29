(ns thermador.data.migration
  (:require [thermador.data.model :as model]
            [thermador.data.migration.page :as page]
            [thermador.config.dropbox :as dbx]
            [clojure.string :as string]))

(def sync-map (into {} [page/page-migration]))

(defn make-id-from-file-name
  [file-name]
  (first (string/split file-name #"\.")))

(defn sync-redis
  []
  (for [[sync-model data] sync-map
        :let [candidate-pairs (dbx/list-files-in-folder (:path data))]]
    (doseq [[file-name path] candidate-pairs
            :let [id (make-id-from-file-name file-name)
                  {:keys [lookup-key create-fn]} data]
            :when (nil? (model/retrieve :lookup-id lookup-key sync-model id))]
      (create-fn id (dbx/load-file-from-dbx path)))))
