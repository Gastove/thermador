(defproject thermador "0.1.0"
  :description "Personal Server and Projects"
  :url "http://thermador.herokuapp.com"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [postgresql "9.3-1102.jdbc4"]
                 [compojure "1.6.0"]
                 [http-kit "2.2.0"]
                 [ring-basic-authentication "1.0.5"]
                 [ring-cors "0.1.10"]
                 [ring/ring-defaults "0.3.0"]
                 [ring/ring-devel "1.6.1"]
                 [environ "1.1.0"]
                 [cheshire "5.7.1"]
                 [com.cemerick/drawbridge "0.0.7"]
                 [com.dropbox.core/dropbox-core-sdk "2.1.1"
                  :exclusions [com.fasterxml.jackson.core/jackson-core]]
                 [com.taoensso/carmine "2.6.2"]
                 [com.taoensso/timbre "3.2.1"]
                 [joda-time/joda-time "2.9.9"]
                 [me.raynes/fs "1.4.6"]
                 [byte-streams "0.2.3"]
                 [jarohen/nomad "0.7.3"]]
  :main ^:skip-aot thermador.web
  :min-lein-version "2.0.0"
  :ring {:handler thermador.web/application-routes
         :init thermador.config.init/init}
  :plugins [[lein-environ "0.5.0"]
            [lein-ring "0.8.11"]]
  :profiles {:uberjar {:main thermador.web
                       :aot :all
                       ;; Drawbridge gets hella mad without this
                       :dependencies [[org.clojure/tools.nrepl "0.2.12"]]}
             :production {:env {:production true
                                :timbre-log-level :info}}
             :dev {:env {:production false
                         :timbre-log-level :debug
                         :port 5000
                         :session-secret "ABRACADABRACADAB"}
                   :dependencies [[ring-mock "0.1.5"]]}}
  )
