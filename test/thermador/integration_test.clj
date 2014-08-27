(ns thermador.integration-test
  (:require [clojure.test :refer :all]
            [taoensso.carmine :as carmine]
            [thermador.config.database :as datastore]
            [thermador.data.model :as model]
            [thermador.data.page :as page]
            [thermador.rest :as rest-api]))


(deftest redis-connection
  (is (= "PONG" (datastore/db (carmine/ping))) "Do we have ping?"))

(defn redis-add-test-data
  []
  (datastore/db (carmine/set "Test:Datum" "hello!")))

(defn redis-cleanup-test-data
  []
  (datastore/db (carmine/del "Test:Datum")))


(deftest redis-get
  (is (= "hello!" (datastore/db (carmine/get "Test:Datum")))))

(defn test-ns-hook []
  (redis-connection)
  (redis-add-test-data)
  (redis-get)
  (redis-cleanup-test-data)
  )
