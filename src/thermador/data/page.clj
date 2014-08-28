(ns thermador.data.page
  (:require ;; [thermador.rest :as rest-api]
            [thermador.data.model :as model]
            [compojure.core :refer [defroutes GET]]
            [cheshire.core :refer [generate-string]]))

(def -PageEntityFields
  {:title ""
   :body ""
   :name ""
   :datum-name ""
   :created-on (model/now)})

(def -PageModelFields
  {:datum-name "Page"})

(def Page (model/create-model -PageModelFields))

(defn create-page
  ([fields]
     (model/create Page -PageEntityFields fields))
  ([name title body & {:keys [datum-name] :or {:datum-name name}}]
     (let [fields {:name name
                   :title title
                   :body body
                   :datum-name datum-name}]
       (model/create Page -PageEntityFields fields))))

;; (defroutes page-routes
;;   (GET "/:id" [id] (rest-api/make-return (model/retrieve :lookup-id :datum-name Page id)))
;;   (GET "/" [] (rest-api/make-return (model/retrieve :all :datum-name Page))))
