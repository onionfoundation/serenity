(ns serenity.db
  "Datomic bootstrap"
  (:require [datomic.api :as d]
            [io.rkn.conformity :as c]
            [serenity.util :as util]))

(defn new-db-uri []
  (str "datomic:mem://" (d/squuid)))

(defn create-db
  ;; This is here in case we want to do any pre/post setup beyond Datomic
  ([]
   (create-db (new-db-uri)))
  ([uri]
   (d/create-database uri)))

(defn bootstrap!
  "Bootstrap schema into the database."
  [uri]
  (create-db uri)
  (let [conn (d/connect uri)]
    (doseq [rsc ["datomic/schema.edn"]]
      (let [norms (util/edn-resource rsc)]
        (c/ensure-conforms conn norms)))))

