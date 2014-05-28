(ns clj-server-test.home
  (:require [compojure.core :refer [defroutes GET]]
            [clojure.java.io :refer [resource]]
            [cheshire.core :refer [generate-string]]))

(def resource-map {
                   :home})

(defn load-post [path]
  (slurp (resource path)))

(defn make-return [id body tags]
  (generate-string {:id id :body body :tags tags}))

(def body (make-return "Home" (load-post "md/home.markdown") "who what where?"))

(defroutes routes
  (GET "/api/home/:id" [:id]
       {:status 200
        :headers {"Content-Type" "text/html"}
        :body body}))
