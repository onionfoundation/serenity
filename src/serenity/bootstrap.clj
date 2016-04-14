(ns serenity.bootstrap
  (:require [serenity.config :refer [config]]
            [serenity.util :as util]
            [serenity.db :as db]))

;; Config setup
;; -------------
(defonce config-map (let [base-map (util/edn-resource "system.edn")]
                      (if (= :unique (:datomic-uri base-map))
                        (assoc base-map
                               :datomic-uri (db/new-db-uri))
                        base-map)))

(defn conf
  ([k]
   (config config-map k))
  ([k not-found]
   (config config-map k not-found)))

