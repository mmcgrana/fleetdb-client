(ns fleetdb.helper)

;; find options

(defn offset [n]
  {:offset n})

(defn limit [n]
  {:limit n})

(defn only [& attr-names]
  (if (= 1 (count attr-names))
    {:only (first attr-names)}
    {:only (vec attr-names)}))

(defn dbdistinct
  ([] (dbdistinct true))
  ([distinct?] {:distinct distinct?}))

(defn asc [attr]
  [attr "asc"])

(defn desc [attr]
  [attr "desc"])

(defn order [& attrs]
  (if (= 1 (count attrs))
    {:order (first attrs)}
    {:order (vec attrs)}))

(defn where-criteria [[c & args]]
  (let [c (keyword c)]
    (condp contains? c
       #{:and :or}
           (vec (cons c (map where-criteria args)))
       #{:= :!= :< :<= :> :>= :in :not-in :>< :><= :>=< :>=<=}
           (vec (cons c args))
       (throw (Exception. (str "Unknown where criteria '" c "'"))))))

(defmacro where [criteria]
  (let [c (where-criteria criteria)]
    `{:where ~c}))

(defn join-options [options]
  (into {} options))

;; queries

(defn auth [password]
  [:auth password])

(defn ping []
  [:ping])

(defn info []
  [:info])

(defn select
  ([collection]
      [:select collection])
  ([collection & find-options]
      [:select collection (join-options find-options)]))

(defn dbcount
  ([collection]
      [:count collection])
  ([collection & find-options]
      [:count collection (join-options find-options)]))

(defn insert [collection & records]
  (if (= 1 (count records))
    [:insert collection (first records)]
    [:insert collection (vec records)]))

(defn update
  ([collection update-map]
      [:update collection update-map])
  ([collection update-map & find-options]
      [:update collection update-map (join-options find-options)]))

(defn delete
  ([collection]
      [:delete collection])
  ([collection & find-options]
      [:delete collection (join-options find-options)]))

(defn drop-collection [collection]
  [:drop-collection collection])

(defn create-index [collection index-spec]
  [:create-index collection index-spec])

(defn drop-index [collection index-spec]
  [:drop-index collection index-spec])

(defn multi-read [& queries]
  [:multi-read (vec queries)])

(defn multi-write [& queries]
  [:multi-write (vec queries)])

(defn checked-write [read-query expected-read-result write-query]
  [:checked-write read-query expected-read-result write-query])

(defn clear []
  [:clear])

(defn explain [query]
  [:explain query])

(defn list-collections []
  [:list-collections])

(defn list-indexes [collection]
  [:list-indexes collection])

(defn compact []
  [:compact])
