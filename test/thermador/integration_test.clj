(ns thermador.integration-test
  (:require [clojure.test :refer :all]
            [clojure.string :as string]
            [taoensso.carmine :as carmine]
            [thermador.config.database :as datastore]
            [thermador.data.model :as model]
            [thermador.data.page :as page]
            [thermador.rest :as rest-api]))

(defn make-key-base
  []
  (let [now (model/now)]
    (str "ThermadorTest:" now ":")))

(defn make-rand-str
  [length]
  (let [upper-alphas "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        lower-alphas (string/lower-case upper-alphas)
        nums "0123456789"
        punct-and-spaces " -!,?:~_ \"'$%&"
        candidate-chars (apply str
                               upper-alphas
                               lower-alphas
                               nums
                               punct-and-spaces)]
    (loop [acc []]
      (if (= (count acc) length)
        (apply str acc)
        (recur (conj acc (rand-nth candidate-chars)))))))

(defn all-in-set?
  "Check if every element in the given seq is present
  in the given set"
  [check-seq in-vec]
  (let [s (fn [x] (some #{x} in-vec))
        mapped-preds (map s check-seq)]
    (reduce #(and (not (nil? %1)) (not (nil? %2))) mapped-preds)))

;; Data spoofing functions.
(def key-base (make-key-base))
(defn make-key
  []
  (str key-base (make-rand-str 15)))
(defn make-val
  []
  (make-rand-str (rand-int 5000)))
(defn make-page
  []
  (let [title (make-rand-str 25)
        body (make-rand-str (rand-int 5000))
        name (string/lower-case (re-find #"\w{1,20}" body))]
    (page/create-page {:title title
                       :name name
                       :body body
                       :datum-name (make-key)})))

(defn redis-add-test-data
  [k v]
  (datastore/db (carmine/set k v)))

(defn redis-cleanup-test-data
  []
  (let [redis-keys (datastore/db (carmine/keys "*ThermadorTest*"))]
    (for [k redis-keys] (datastore/db (carmine/del k)))))

(deftest redis-get-set-and-clear
  (let [k (make-key)
        v (make-val)
        response (datastore/db (carmine/set k v))]
    (is (= "OK" response) "Can we set a value to a key?")
    (is (= v (datastore/db (carmine/get k))) "Can we retrieve the value of a key?")
    (is (= 1 (datastore/db (carmine/del k))) "Can we delete the value at a key?")
    (is (nil? (datastore/db (carmine/get k)))) "Is the value really deleted?"))

(deftest create-and-destroy-pages
  (let [page (make-page)
        key-fn (partial model/make-key :datum-name)
        page-key (key-fn @page)
        page-model-key (key-fn page/Page)
        pages (take 5 (repeatedly make-page))
        pages-keys (into [] (map #(key-fn (deref %)) pages))
        db-set-keys (datastore/db (carmine/smembers page-model-key))]
    (is (not (nil? (some #{page-key} db-set-keys)))
        "The set of page keys should contain the single page key")
    (is (all-in-set? pages-keys db-set-keys)
        "The set of page keys should contain the five generated page keys")
    (is (= @page (deref (model/retrieve :key page-key))) "Do we get the right page back?")
    (is (= (set (map deref pages)) (set (map deref (model/retrieve :keys pages-keys))))
        "Can we retrieve many things at once?")
    (redis-cleanup-test-data)))

;; (defn test-ns-hook []
;;   (redis-connection)
;;   (redis-add-test-data)
;;   (redis-get)
;;   (redis-cleanup-test-data)
;;   )
