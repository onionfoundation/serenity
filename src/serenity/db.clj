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


(comment
  (def test-db-uri (new-db-uri))
  (bootstrap! test-db-uri)
  (def conn (d/connect test-db-uri))

  ;; Let's transact some users
  ;;  .. but there's a small issue: Users have a ref to Org,
  ;; and Org has a ref to Address.  To get started we'll need an address, then
  ;; an org, then we can add users.
  ;; To create all the data in a single transaction, we can create temp-ids in
  ;; our process, and Datomic will honor the relationships when we transact.
  ;;
  ;; A transaction is a vector, with the various entities (facts about entities)
  ;; you want to transact.
  (let [address-id (d/tempid :db.part/user)
        org-id (d/tempid :db.part/user)]
    (d/transact conn [{:db/id address-id
                       :address/street "123 Evergreen Drive"
                       :address/city "Bangor"
                       :address/state "Maine"
                       :address/postal "04401"}
                      {:db/id org-id
                       :org/name "Plant a Tree Foundation"
                       :org/address address-id
                       :org/email "contact@treefoundation.org"
                       :org/ein "1234567890"}
                      {:db/id #db/id[:db.part/user] ;; If you don't care about the tempid, you can use this literal form
                       :user/first-name "Joe"
                       :user/last-name "Smith"
                       :user/email "joe.smith@gmail.com"
                       :user/org org-id
                       :user/roles [:user.role/user
                                    :user.role/grantor
                                    :user.role/grantor.admin]}]))

  ;; Now we can query...
  ;; Find me all org names for users named "Joe"
  ;; Note: It's ok to store Datomic connections, but do not store DB values (**they are a single point in time**)
  ;;       Instead, get the current/latest DB with `(d/db my-datomic-connection)`
  (d/q '[:find ?org-name
         :in $
         :where
         [?user :user/first-name "Joe"]
         [?user :user/org ?org]
         [?org :org/name ?org-name]]
       (d/db conn))

  ;; That worked, but the results are packed in a vector, all in a set
  ;; We can tell Datomic we're expect back a single name with `.` or a list of names with `...`
  (d/q '[:find ?org-name .
         :in $
         :where
         [?user :user/first-name "Joe"]
         [?user :user/org ?org]
         [?org :org/name ?org-name]]
       (d/db conn))
  (d/q '[:find [?org-name ...]
         :in $
         :where
         [?user :user/first-name "Joe"]
         [?user :user/org ?org]
         [?org :org/name ?org-name]]
       (d/db conn))

  ;; You can also use Datomic's Pull API...

  )
