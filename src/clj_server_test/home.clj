(ns clj-server-test.home
  (:use [compojure.core :only [defroutes GET]]
        [clojure.java.io :only [resource]]))

(defroutes routes
  (GET "/api/home" []
       {:status 200
        :headers {"Content-Type" "text/html"}
        :body (slurp (resource "md/home.markdown"))}))
