(ns clj-server-test.config.database
  (:require [environ.core :refer [env]]))

(def DB (or (env :database-url) "postgres://localhost:5432/clj-server-test"))
