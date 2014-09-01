(ns thermador.rest-integration-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as json]
            [ring.mock.request :as mock]
            [taoensso.timbre :as log]
            [thermador.data-faking :as fake]
            [thermador.data.page :as page]
            [thermador.redis-test-utils :refer [cleanup-test-data]]
            [thermador.web :as web]))

(deftest rest-get-five-pages-test
  (log/debug "Testing GET against /api/page")
  (let [test-page-vector (into [] (take 5 (repeatedly fake/make-page)))
        ret (web/application-routes (mock/request :get "/api/page"))
        returned-pages (json/parse-string (:body ret) true)
        names-in (into #{} (map (comp :name deref) test-page-vector))
        returned-names (into #{} (map :name returned-pages))]
    (is (= 200 (:status ret)) "Should get a 200 status")
    ;;(is (= 5 (count returned-pages)) "Should get back five things")
    (is (= (count names-in) (count (clojure.set/intersection names-in returned-names))))
    "The returned set should contain all the names we put in.")
  (cleanup-test-data))

(deftest rest-get-single-page-test
  (log/debug "Testing GET against /api/page/:id")
  (let [test-page (fake/make-page)
       expected-map {:status 200
                     :headers {"Content-Type" "text/html"}
                     :body (page/make-rest-return test-page)}
       k (:datum-name @test-page)
       route (str "/api/page/" k)
       response (web/application-routes (mock/request :get route))]
   (is (and
        (= (:status response) (:status expected-map))
        (= (json/parse-string (:body response))) (:body expected-map))
       "Should return the specific page requested with a 200 status."))
  (cleanup-test-data))
