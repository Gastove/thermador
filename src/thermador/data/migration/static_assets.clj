(ns thermador.data.migration.static-assets
  (:require [byte-streams :as bs]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [me.raynes.fs :as fs]
            [taoensso.timbre :as log]))

;; TODO: currently I don't have a convincing way to remove a static asset of
;; this kind. Also not doing "updating." For images/gifs, this should be fine?
;; For now? I hope? - RMD 2017-06-01

(def static-assets-dir "/tmp/gifs")

(defn load-gif [file-name file-stream]
  (log/info "Loading new gif: " file-name)
  (let [new-path (str/join "/" [static-assets-dir file-name])
        new-file (io/file new-path)]
    (bs/transfer file-stream new-file)))

(defn find-gif [name]
  (let [path-to-check (str/join "/" [static-assets-dir name])]
    (log/debug "Looking for gif: " path-to-check)
    (fs/exists? path-to-check)))

(def gif-migration
  {:gifs {:path "/gifs/"
          :create-fn load-gif
          :exists-fn find-gif}})
