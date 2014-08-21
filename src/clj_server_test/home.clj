(ns thermador.home
  (:require [compojure.core :refer [defroutes GET]]
            [clojure.java.io :refer [resource file]]
            [cheshire.core :refer [generate-string]]
            [thermador.models.page :as page]))

(defn make-return [posts]
  (let [body (generate-string (for [{:keys [id name title body created_on]} posts]
                                {:id id
                                 :name name
                                 :body body
                                 :title title
                                 :created_on created_on}))
        status 200
        headers {"Content-Type" "text/html"}]
    {:status status
     :headers headers
     :body body}))

(defroutes routes
  (GET "/:id" [id] (make-return (page/get-by-name id)))
  (GET "/" [] (make-return (page/get-all))))
