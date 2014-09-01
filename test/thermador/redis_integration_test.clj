(ns thermador.redis-integration-test
  (:require [clojure.test :refer :all]
            [taoensso.carmine :as carmine]
            [taoensso.timbre :as log]
            [thermador.config.database :as datastore]
            [thermador.data-faking :as fake]
            [thermador.data.model :as model]
            [thermador.data.page :as page]
            [thermador.redis-test-utils :as redis-utils]))

(defn all-in-set?
  "Check if every element in the given seq is present
  in the given set"
  [check-seq in-vec]
  (let [s (fn [x] (some #{x} in-vec))
        mapped-preds (map s check-seq)]
    (reduce #(and (not (nil? %1)) (not (nil? %2))) mapped-preds)))

(deftest redis-get-set-and-clear
  (log/debug "Testing Redis getting/setting.")
  (let [k (fake/make-key)
        v (fake/make-val)
        response (datastore/db (carmine/set k v))]
    (is (= "OK" response) "Can we set a value to a key?")
    (is (= v (datastore/db (carmine/get k))) "Can we retrieve the value of a key?")
    (is (= 1 (datastore/db (carmine/del k))) "Can we delete the value at a key?")
    (is (nil? (datastore/db (carmine/get k)))) "Is the value really deleted?"))

(deftest create-and-destroy-pages
  (log/debug "Testing ability to create and destroy pages in Redis.")
  (let [page (fake/make-page)
        page-key (model/make-key @page)
        page-model-key (model/make-key page/Page)
        pages (take 5 (repeatedly fake/make-page))
        pages-keys (into [] (map #(model/make-key (deref %)) pages))
        db-set-keys (datastore/db (carmine/smembers page-model-key))]
    (is (not (nil? (some #{page-key} db-set-keys)))
        "The set of page keys should contain the single page key")
    (is (all-in-set? pages-keys db-set-keys)
        "The set of page keys should contain the five generated page keys")
    (is (= @page (deref (model/retrieve :key page-key)))
        "Do we get the right page back?")
    (is (= (set (map deref pages))
           (set (map deref (model/retrieve :keys pages-keys))))
        "Can we retrieve many things at once?")
    (is (and (true? (model/delete page :datum-name))
             (nil? (model/retrieve :key page-key)))
        "Does deleting a single page report success, and does it work?")
    (is (true? (reduce #(and %1 %2) (model/delete pages)))
        "Does deleting many pages work?")
    (redis-utils/cleanup-test-data)))
