(ns thermador.data.migration
  (:require [thermador.data.migration.page :as page]
            [clojure.string :as string]))

(def- migration-map {})

(defn sync-redis
  [sync-map]
  (for [[model data] sync-map
        :let [candidate-pairs (dbx/list-files-in-folder (:path data))]]
    (doseq [[id path] candidate-pairs
            :let [{:keys [lookup-key candidate-model create-fn]} data]
            :when (nil? (model/retrieve :lookup-id lookup-key candidate-model id))]
      (create-fn (dbx/load-file-from-dbx path)))))

(defn make-title-from-md
  [md]
  (string/trim (second (re-find #"([\w ]+)" md))))

(defn make-id-from-file-name
  [file-name]
  (first (string/split file-name #"\.")))
