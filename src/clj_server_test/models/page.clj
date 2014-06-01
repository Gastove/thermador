(ns clj-server-test.models.page
  (:require [clojure.java.jdbc :as sql]))

(def table-name "pages")

(def table-ddl (sql/create-table-ddl
                :pages
                [:id :serial "PRIMARY KEY"]
                [:name :varchar]
                [:body :text]
                [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]))

(def db-properties {:table-name table-name
                    :table-ddl table-ddl})
