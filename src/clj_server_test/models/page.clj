(ns clj-server-test.models.page
  (:require [clojure.java.jdbc :as sql]
            [clj-server-test.config.database :refer [DB]]
            [clj-server-test.config.dropbox :as dbx]))

(def table-name "pages")

(def table-ddl (sql/create-table-ddl
                :pages
                [:id :serial "PRIMARY KEY"]
                [:name :varchar]
                [:body :text]
                [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]))

(def db-properties {:table-name table-name
                    :table-ddl table-ddl})

;; TODO: Get this the hell out of this ns :-P
;; TODO: Need a "update text in DB" function.

(defn add-page []
  (let [prompt (fn [word] (println (str word "?"))(read-line))
        name (prompt "Name")
        path (prompt "Path")
        body (dbx/load-file-from-dbx path)]
    (sql/insert! DB :pages {:name name :body body})))
