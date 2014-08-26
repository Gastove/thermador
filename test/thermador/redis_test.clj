(ns thermador.redis-test
  (:require [clojure.test :refer :all]
            [taoensso.carmine :as carmine]
            [thermador.config.database :as datastore]))

(deftest placeholder-test
  (is true "I hope true is true"))

(deftest redis-connection
  (is (= "PONG" (datastore/db (carmine/ping))) "Do we have ping?"))
