(ns serenity.routes
  (:require [clojure.java.io :as io]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [datomic.api :as d]
            [ring.util.response :as ring-resp]
            [serenity.interceptor :as interceptor]
            [serenity.bootstrap :refer [conf]]))

(defn health-check
  [request]
  (ring-resp/response "Hello, from Serenity!"))

(def app-resp (slurp (io/resource "public/index.html")))
(defn app-page
  [request]
  (ring-resp/response app-resp))


(defroutes routes
  [[["/" {:get health-check}
     ^:interceptors [(interceptor/insert-datomic (conf :datomic-uri))]
     ["/app" {:get app-page}]]]])

