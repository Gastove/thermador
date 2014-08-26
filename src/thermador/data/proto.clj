(ns thermador.data.proto
  "A data model. Prototypes."
  (:refer-clojure :rename {get core-get
                           extend core-extend}))

(defn beget
  [parent]
  {:prototype parent})

(defn get
  [pobj k]
  (if-let [res (k pobj)]
    res
    (if-let [proto (:prototype pobj)]
      (recur proto k)
      nil)))

(defn extend
  [pobj extention]
  {:pre [(map? extention)
         (contains? pobj :prototype)]}
  (into pobj extention))
