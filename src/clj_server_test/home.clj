(ns clj-server-test.home
  (:require [compojure.core :refer [defroutes GET]]
            [clojure.java.io :refer [resource file]]
            [cheshire.core :refer [generate-string]]
            [clj-server-test.models.page :as page]))

(defn make-return [posts]
  (let [body (for [{:keys [id body]} posts] (generate-string {:id id :body body}))
        status 200
        headers {"Content-Type" "text/html"}]
    {:status status
     :headers headers
     :body body}))

(defroutes routes
  (GET "/api/home/:id" [id] (make-return (page/get-by-name id))
  (GET "/api/home/" [] (make-return (page/get-all)))))
