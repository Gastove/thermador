(ns clj-server-test.home
  (:require [compojure.core :refer [defroutes GET]]
            [clojure.java.io :refer [resource]]
            [cheshire.core :refer [generate-string]]))

(defn slurp-from-location
  "Slurp the contents of a resource at a given location "
  [location]
  (slurp (resource location)))

(defn load-resource-make-path
  "Load a page from a location, given "
  [location resource extension]
  (let [stresource (name resource)])
  (slurp-from-location (str location "/" resource "." extension)))

(defn load-md-page
  [page]
  (load-resource-make-path "md" page "markdown"))

(defn make-return
  [id]
  (let [body (load-md-page id)
        status 200
        headers {"Content-Type" "text/html"}
        json-payload (generate-string {:id id :body body})]
    {:status status
     :headers headers
     :body body}))

(defroutes routes
  (GET "/api/home/:id" [id] (make-return id)
       ))
