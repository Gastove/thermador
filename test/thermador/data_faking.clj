(ns thermador.data-faking
  (:require [clojure.string :as string]
            [thermador.data.model :as model]
            [thermador.data.page :as page])
  (:import [org.joda.time.format DateTimeFormat]))

(defn make-key-base
  []
  (let [now (model/now)
        formatter (DateTimeFormat/forPattern "yyyy-MM-dd'T'HH:mm:ss")]
    (str "thermador-test:" (.print formatter now) ":")))

(def key-base (make-key-base))

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
                       :datum-name (str key-base name)
                       })))
