(ns thermador.data.migration.page
  (:require [clojure.string :as str]
            [thermador.data
             [model :as model]
             [page :as page]]))

(defn make-title-from-md-file-name
  [md]
  (-> md
      (str/split #"\.")
      (first)))

(defn create-page-from-markdown-text
  [name markdown-stream]
  (let [title (make-title-from-md-file-name name)
        markdown (slurp markdown-stream)]
    (page/create-page name title markdown)
    (.close markdown-stream)))

(defn update-page
  [lookup-key model id markdown-stream]
  (let [pobj (model/retrieve :lookup-id lookup-key model id)
        markdown (slurp markdown-stream)
        f #(assoc % :body markdown)]
    (model/transform pobj f)
    (.close markdown-stream)))

(def page-migration
  {page/Page {:path "/the_range/markdown"
              :lookup-key page/lookup-key
              :create-fn create-page-from-markdown-text
              :update-fn update-page}})
