(ns thermador.models.page
  (:require [clojure.java.jdbc :as sql]
            [thermador.config.database :refer [DB]]
            [thermador.config.dropbox :as dbx]))

(def table-name "pages")

(def table-ddl (sql/create-table-ddl
                :pages
                [:id :serial "PRIMARY KEY"]
                [:name :varchar]
                [:title :varchar]
                [:body :text]
                [:created_on :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]))

(def db-properties {:table-name table-name
                    :table-ddl table-ddl})

;; TODO: Get this the hell out of this ns :-P
;; TODO: Need a "update text in DB" function.

(defn get-all []
  (sql/query DB ["SELECT * FROM pages"]))

(defn get-by-name [name]
  (sql/query DB ["SELECT * FROM pages WHERE name = ?" name]))

(defn prompt [word]
  (println (str word "\n--> "))
  (let [answer (read-line)]
    (print answer)
    answer))

(defn print-then-prompt [message word]
  (println message)
  (prompt word))

(defn add-page []
  (let [path (prompt "Path?")
        body (dbx/load-file-from-dbx path)
        name (prompt "Name?")
        title (prompt "Title?")]
    (sql/insert! DB :pages {:name name :body body :title title})))

(defn update-page []
  (let [all-page-names (map :name (get-all))
        page-list (reduce #(apply str %1 "-" %2 "\n") "" all-page-names)
        page-name (print-then-prompt page-list "Update?")
        path (prompt "Where's the new markdown")
        new-body (dbx/load-file-from-dbx path)]
    (sql/update! DB table-name {:body new-body} ["name = ?" page-name])))
