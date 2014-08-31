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
