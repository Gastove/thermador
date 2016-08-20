(ns thermador.data.migration.page
  (:require [clojure.string :as str]
            [thermador.data
             [model :as model]
             [page :as page]]))

(defn make-title-from-md
  [md]
  (let [words (-> md
                  (str/split #"\.")
                  (first)
                  (str/split #"_"))]
    (->> words
         (map str/capitalize)
         (interpose " ")
         (apply str))))

(defn create-page-from-markdown-text
  [name markdown]
  (let [title (make-title-from-md markdown)]
    (page/create-page name title markdown)))

(defn update-page
  [lookup-key model id markdown]
  (let [pobj (model/retrieve :lookup-id lookup-key model id)
        f #(assoc % :body markdown)]
    (model/transform pobj f)))

(def page-migration
  {page/Page {:path "/the_range/markdown"
              :lookup-key page/lookup-key
              :create-fn create-page-from-markdown-text
              :update-fn update-page}})
