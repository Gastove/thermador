(ns thermador.rest-test
  (:require [clojure.test :refer :all]
            [cheshire.core :refer [generate-string]]
            [thermador.rest :as rest-api]))

(def test-pobj
  (atom {:name "test"
         :field "value"}))

(def test-pobj-vector
  [test-pobj test-pobj test-pobj])

(def expected-map
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (generate-string @test-pobj)})

(def expected-many-vector
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (generate-string (into [] (map deref test-pobj-vector)))})



(deftest test-make-return
  (testing "Return one thing"
    (is (= (rest-api/make-return test-pobj) expected-map) ""))
  (testing "Return a list of things"
    (is (= (rest-api/make-return test-pobj-vector) expected-many-vector))))
