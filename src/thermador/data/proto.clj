(ns thermador.data.proto
  (:refer-clojure :exclude [get extend]))

(defn beget
  [parent]
  (assoc {} :prototype parent))

(defn get
  [obj k]
  (if-let [res (k obj)]
    res
    (if-let [proto (:prototype obj)]
      (get proto k)
      nil)))

(defn extend
  [obj extention]
  {:pre [(map? extention)
         (contains? obj :prototype)]}
  (into obj extention))
