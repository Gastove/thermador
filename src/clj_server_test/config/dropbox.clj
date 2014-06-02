(ns clj-server-test.config.dropbox
  (:require [environ.core :refer [env]])
  (:import [com.dropbox.core.DbxRequestConfig]
           [com.dropbox.core.DbxClient]
           [java.util.Locale]
           [java.io.ByteArrayOutputStream]))

(def app-key (env :dbx-app-key))
(def app-secret (env :dbx-app-secret))
(def access-token (env :dbx-access-token))

(defn get-dbx-config []
  (let [name "TheRange/1.0"
        locale (.. (java.util.Locale/getDefault) toString)]
    (DbxRequestConfig. name locale)))

(defn get-dbx-client []
  (let [config (get-dbx-config)]
    (DbxClient. config access-token)))

(defn load-file-from-dbx [file-path]
  (let [client (get-dbx-client)]
    (with-open [stream (java.io.ByteArrayOutputStream.)]
      (.getFile client file-path nil stream)
      (.toString stream))))
