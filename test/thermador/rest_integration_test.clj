(ns thermador.rest-integration-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as json]
            [ring.mock.request :as mock]
            [taoensso.timbre :as log]
            [thermador.data-faking :as fake]
            [thermador.data.page :as page]
            [thermador.redis-test-utils :refer [cleanup-test-data]]
            [thermador.web :as web]))

(deftest rest-get-page-test
  (let [test-page (fake/make-page)
        test-page-vector (take 5 (repeatedly fake/make-page))
        expected-map {:status 200
                      :headers {"Content-Type" "text/html"}
                      :body (page/make-rest-return test-page)}]
    (log/debug "Page name is" (:datum-name @test-page))
    (let [ret (web/application-routes (mock/request :get "/api/page"))
          returned-pages (json/parse-string (:body ret) true)
          names-in (into #{(:name @test-page)} (map (comp :name deref) test-page-vector))
          returned-names (into #{} (map :name returned-pages))]
      (is (= 200 (:status ret)) "Should get a 200 status")
      (is (= 6 (count returned-pages)) "Should get back six things")
      (is (= names-in returned-names)) "Should get back the same names we put in")
    (let [k (:datum-name @test-page)
          route (str "/api/page/" k)
          response (web/application-routes (mock/request :get route))]
      (log/debug "Trying to retrieve:" k)
      (is (and
           (= (:status response) (:status expected-map))
           (= (json/parse-string (:body response))) (:body expected-map))
          "Should return the specific page requested with a 200 status.")))
  (cleanup-test-data))
