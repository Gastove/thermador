(ns thermador.model-test
  (:require [clojure.test :refer :all]
            [taoensso.timbre :as log]
            [thermador.data.model :as model]))

(deftest model-test-make-key
  (log/debug "Test key-making")
  (let [test-map {:prototype {:prototype {:datum-name "First"}
                              :datum-name "Second"}
                  :datum-name "Third"}]
    (is (= "First:Second:Third" (model/make-key test-map)))
    (is (= "First:Second:Dog" (model/make-key (:prototype test-map) "Dog")))))


;; {:prototype {:prototype {:datum-name "thermador",
;;                          :created-on #<DateTime 2014-08-31T01:48:11.037Z>,
;;                          :lookup-key :datum-name},
;;              :datum-name "page"},
;;  :title "qnBn%yWK9LHT$CCkYZ\"~jCiDi",
;;  :body,
;;  :name "itg8jisqog",
;;  :datum-name "thermador-test:2014-08-31T01:48:11:itg8jisqog",
;;  :created-on #<DateTime 2014-08-31T01:48:11.655Z>}
