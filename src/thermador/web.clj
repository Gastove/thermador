(ns thermador.web
  (:require [cemerick.drawbridge :as drawbridge]
            [clojure.java.io :as io]
            [compojure.core :refer [ANY context defroutes DELETE GET POST PUT]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [environ.core :refer [env]]
            [org.httpkit.server :as http-kit]
            [ring.middleware.basic-authentication :as basic]
            [ring.middleware.cors :as cors]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.reload :as reload]
            [ring.middleware.session :as session]
            [ring.middleware.session.cookie :as cookie]
            [ring.middleware.stacktrace :as trace]
            [taoensso.timbre :as log]
            [thermador.rest :as rest-api]))

(defn- authenticated? [user pass]
  (= [user pass] [(env :repl-user false) (env :repl-password false)]))

(def ^:private drawbridge
  (-> (drawbridge/ring-handler)
      (session/wrap-session)
      (basic/wrap-basic-authentication authenticated?)))

(defroutes application-routes
  (context "/api" [] rest-api/rest-routes)
  (ANY "/repl" {:as req}
       (drawbridge req))
  (ANY "/log-test" [] (log/info "Boop!"))
  (GET "/" []
       {:status 200
        :headers {"Content-Type" "text/plain"}
        :body (pr-str ["Hello" :from 'Heroku])})
  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(def application
  (-> (wrap-defaults application-routes api-defaults)
      reload/wrap-reload
      (cors/wrap-cors :access-control-allow-origin #".*")))

(defn wrap-error-page [handler]
  (fn [req]
    (try (handler req)
         (catch Exception e
           {:status 500
            :headers {"Content-Type" "text/html"}
            :body (slurp (io/resource "500.html"))}))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))
        ip (or (env :thermador-ip) "0.0.0.0")
        store (cookie/cookie-store {:key (env :session-secret)})]
    (http-kit/run-server (-> application
                             ((if (env :production)
                                wrap-error-page
                                trace/wrap-stacktrace))
                             (site {:session {:store store}}))
                         {:port port :ip ip})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
