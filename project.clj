(defproject thermador "1.0.0-SNAPSHOT"
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
                 [cheshire "5.3.1"]
                 [environ "0.5.0"]
                 [com.cemerick/drawbridge "0.0.6"]
                 [com.dropbox.core/dropbox-core-sdk "1.7.4"]
                 [com.taoensso/carmine "2.6.2"]
                 [joda-time/joda-time "2.4"]
                 ;[midje "1.6.3"]
                 ]
  :min-lein-version "2.0.0"
  :plugins [[lein-environ "0.5.0"]
            [lein-ring "0.8.11"]]
  :ring {:handler thermador.web/application-routes}
  :profiles {:production {:env {:production true}}
             :dev {:env {:production false}
                   :dependencies [[midje "1.6.3"]]}}
  )
