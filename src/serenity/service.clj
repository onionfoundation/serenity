(ns serenity.service
  (:require [io.pedestal.http :as http]
            [ns-tracker.core :refer [ns-tracker]]
            [serenity.routes :refer [routes]]
            [serenity.db :as db]
            [serenity.bootstrap :refer [conf]])
  (:gen-class))


(defonce modified-namespaces
  (if (conf :prod)
    (constantly nil)
    (ns-tracker ["src"])))

(def service
  {::http/host (conf :host "127.0.0.1")
   ::http/port (Integer/parseInt (conf :port "8080"))
   ::http/type :jetty
   ::http/join? false
   ::http/resource-path "/public"
   ::http/routes (if (conf :prod)
                   routes
                   (fn []
                     (doseq [ns-sym (modified-namespaces)]
                       (require ns-sym :reload))
                     (deref #'routes)))})

(defn server [service-overrides]
  (http/create-server (merge service
                             service-overrides)))

(defn start
  ([service-overrides]
   (let [server (server service-overrides)
        datomic-uri (:datomic-uri service-overrides
                                  (conf :datomic-uri))]
    (db/bootstrap! datomic-uri)
    (http/start server)))
  ([k & vkvs]
   (start (apply hash-map (cons vkvs k)))))

(defn stop [serv]
  (if serv
    (http/stop serv)
    serv))

(defn restart [serv]
  (if serv
    (http/start (stop serv))
    serv))

(defn run-dev
  "The entry-point for 'lein run-dev'"
  [& args]
  (println "\nCreating your [DEV] server...")
  (-> service ;; start with production configuration
      (merge {:env :dev
              ;; do not block thread that starts web server
              ::http/join? false
              ;; Routes can be a function that resolve routes,
              ;;  we can use this to set the routes to be reloadable
              ::http/routes (fn []
                              (doseq [ns-sym (modified-namespaces)]
                                (require ns-sym :reload))
                              (deref #'routes))
              ;; all origins are allowed in dev mode
              ::http/allowed-origins {:creds true :allowed-origins (constantly true)}}
             (apply hash-map args))
      ;; Wire up interceptor chains
      http/default-interceptors
      http/dev-interceptors
      server
      start))

(defn -main [& args]
  (start ::http/join? true
         ::http/routes routes))

