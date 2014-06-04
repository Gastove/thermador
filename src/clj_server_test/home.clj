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

(def poot {:status 200, :headers {"Content-Type" "text/html"}, :body "[{\"id\":1,\"body\":\"### Hello!\\nOMFG IT'S WORKING.\\n\\nIt's the Internet; this is a profile! And some links? Eh? Eh?\\n\\nI'm a data scientist and hacker. I live in Oakland, California; I work in tech. I crunch numbers and swear swears, and then I take a break from numbers to have a drink and read. I write Many Codes, and backpack many places, but I only ride one bicycle at a time.\\n\\nI'm currently figuring out this site; it's developing quickly, so check back periodically. While I'm working on that, you might want to read more [about me](/about), or checkout my [github](http://www.github.com/Gastove). I've also got not one but _two_ blogs! The [more professional one](http://blog.gastove.com) is about code and data science; the [less professional one](http://food.gastove.com) is about food and eating. They are both very pleased to meet you.\\n\"}]"})

(defroutes routes
  (GET "/api/home/:id" [id] (make-return (page/get-by-name id)))
  (GET "/api/home/" [] (make-return (page/get-all))))
