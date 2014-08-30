(defproject thermador "0.1.0"
  :description "Personal Server and Projects"
  :url "http://thermador.herokuapp.com"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/java.jdbc "0.3.3"]
                 [postgresql "9.3-1101.jdbc4"]
                 [compojure "1.1.1"]
                 [ring/ring-jetty-adapter "1.1.0"]
                 [ring/ring-devel "1.1.0"]
                 [ring-basic-authentication "1.0.1"]
                 [ring-cors "0.1.0"]
                 [environ "0.5.0"]
                 [com.cemerick/drawbridge "0.0.6"]
                 [com.dropbox.core/dropbox-core-sdk "1.7.4"
                  :exclusions [com.fasterxml.jackson.core/jackson-core]]
                 [com.taoensso/carmine "2.6.2"]
                 [com.taoensso/timbre "3.2.1"]
                 [joda-time/joda-time "2.4"]]
  :min-lein-version "2.0.0"
  :ring {:handler thermador.web/application-routes
         :init thermador.config.init/init}
  :plugins [[lein-environ "0.5.0"]
            [lein-ring "0.8.11"]]
  :profiles {:production {:env {:production true
                                :timbre-log-level :info}}
             :dev {:env {:production false
                         :timbre-log-level :debug
                         :port 5000}
                   :dependencies [[cheshire "5.3.1"]
                                  [ring-mock "0.1.5"]]}}
  )
