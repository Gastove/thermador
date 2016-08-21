(ns thermador.config.dropbox
  (:require [environ.core :refer [env]]
            [clojure.string :as str])
  (:import [com.dropbox.core DbxRequestConfig]
           [com.dropbox.core.v2 DbxClientV2]
           [java.util.Locale]
           [java.io.ByteArrayOutputStream]))

(def app-key (env :dbx-app-key))
(def app-secret (env :dbx-app-secret))
(def access-token (env :dbx-access-token))

(defn get-dbx-config
  []
  (let [name "Thermador/1.0"
        locale (.. (java.util.Locale/getDefault) toString)]
    (DbxRequestConfig. name locale)))

(defn get-dbx-client
  []
  (let [config (get-dbx-config)]
    (DbxClientV2. config access-token)))

(defn get-dbx-file-client
  "The `files' client is what actually provides file access methods."
  [client]
  (.files client))

(defn load-file-from-dbx
  [file-path]
  (let [client (get-dbx-client)
        file-client (get-dbx-file-client client)
        download-client (.download file-client file-path)]
    (with-open [stream (.getInputStream download-client)]
      (slurp stream))))

(defn list-folder-contents
  [path]
  (let [client (get-dbx-client)
        file-client (get-dbx-file-client client)
        contents (.listFolder file-client path)]
    (for [metadata (.getEntries contents)
          ;; Sometimes hidden files get in there, briefly. Do *not* add them to the DB!
          :when (not (-> (.getName metadata) (str/starts-with? ".")))]
      [(.getName metadata) (.getPathLower metadata)])))

(defn list-files-in-folder
  [path]
  (let [metadata (list-folder-contents path)
        listing (.children metadata)]
    (into [] (for [item listing
                   :when (.isFile item)]
               [(.name item) (.path item)]))))
