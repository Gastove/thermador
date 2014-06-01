(ns clj-server-test.config.migration
  (:require [clj-server-test.config.database :refer [DB]]
            [clojure.java.jdbc :as sql]
            [clj-server-test.models.page :as page]))

(defn migrated?
  [table]
  (-> (sql/query DB [(str  "SELECT COUNT(*) FROM information_schema.tables "
                           "WHERE table_name = ?") table])
      first :count pos?))

(defn migrate
  [{:keys [table-name table-ddl]}]
  (if (migrated? table-name)
    (println (str "Table " table " already migrated."))
    (do
      (print (str "Migrating table " table-name "...  "))
      (flush)
      (sql/db-do-commands DB table-ddl)
      (println "Done"))))

(def known-models [page/db-properties])

(defn migrate-known-models
  []
  (map #(migrate %) known-models))
