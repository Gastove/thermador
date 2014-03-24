(ns clj-server-test.home
  (:require [compojure.core :refer [defroutes GET]]
            [clojure.java.io :refer [resource]]
            [cheshire.core :refer [generate-string]]))

(defn load-post [path]
  (slurp (resource path)))

(defn make-return [title body tags]
  (generate-string {:title title :body body :tags tags}))

(def body (make-return "Home" (load-post "md/home.markdown") "who what where?"))

(defroutes routes
  (GET "/api/home" []
       {:status 200
        :headers {"Content-Type" "text/html"}
        :body body}))
