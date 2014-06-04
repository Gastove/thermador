(ns clj-server-test.home
  (:require [compojure.core :refer [defroutes GET]]
            [clojure.java.io :refer [resource file]]
            [cheshire.core :refer [generate-string]]
            [clj-server-test.models.page :as page]))

(defn make-return [posts]
  (let [body (generate-string (for [{:keys [id body]} posts] {:id id :body body}))
        status 200
        headers {"Content-Type" "text/html"}]
    {:status status
     :headers headers
     :body body}))

(defroutes routes
  (GET "/:id" [id] (make-return (page/get-by-name id)))
  (GET "/" [] (make-return (page/get-all))))
