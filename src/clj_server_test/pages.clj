(ns clj-server-test.pages
  (:require [compojure.core  :refer [defroutes ALL GET]]
            [clojure.java.io :refer [resources]]
            [cheshir.core    :refer [generate-string]]
            ))

(def resource-map {:home  ["md", "markdown"]
                   :about ["md", "markdown"]
                   :cv    ["md", "markdown"]})

(defn make-file-path [id, r-map]
  (let [[[folder, extention]] (r-map id)
        file-name ()]
    (apply str [folder file-name "." extention])
    ))

(defn load-file [file-path]
  (slurp (resource file-path)))

(defn make-return [& :keys {:id :text}]
  (generate-string {id :id text :text}))

(defroutes routes
  (ALL "/" [])
  (GET "/:id" [:id]))
