(ns serenity.routes
  (:require [clojure.java.io :as io]
            [datomic.api :as d]
            [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [ring.util.response :as ring-resp]
            [geheimtur.interceptor :as auth]
            [geheimtur.impl.oauth2 :as oauth2]
            [geheimtur.util.auth :as auth-util]
            [serenity.interceptor :as interceptor]
            [serenity.bootstrap :refer [conf]]))


(defn google-oauth-success
  [token auth-map]
  (let [{:keys [identity-map return]} auth-map
        user {:name (:displayName identity-map)
              :roles #{:user}
              :full-name (:displayName identity-map)}]
    (->
      (ring-resp/redirect return)
      (auth-util/authenticate user))))

(def oauth-providers
  {:google {:auth-url "https://accounts.google.com/o/oauth2/auth"
            :client-id (conf :google-oauth-id "client-id")
            :client-secret (conf :google-oauth-secret "client-secret")
            :callback-uri "http://TODO/oauth.callback"
            :scope "profile email"
            :token-url "https://accounts.google.com/o/oauth2/token"
            :user-info-url "https://www.googleapis.com/plus/v1/people/me"
            :on-success-handler google-oauth-success}})

(defn health-check
  [request]
  (ring-resp/response "Hello, from Serenity!"))

(def app-resp (slurp (io/resource "public/index.html")))
(defn app-page
  [request]
  (ring-resp/response app-resp))

(defn whoami-data
  [request]
  (ring-resp/response (or (auth-util/get-identity request)
                          {:roles #{}})))

(defroutes routes
  [[["/" {:get app-page}
     ^:interceptors  [(body-params/body-params)
                      http/html-body]
     ["/status" {:get health-check}]
     ["/oauth.login" {:get (oauth2/authenticate-handler oauth-providers)}]
     ["/oauth.callback" {:get (oauth2/callback-handler oauth-providers)}]
     ["/auth" ^:interceptors [(auth/interactive {})]]
     ["/api" ^:interceptors  [interceptor/attach-received-time
                              interceptor/attach-request-id
                              (auth/guard :silent? false :roles #{:user})
                              (interceptor/insert-datomic (conf :datomic-uri))
                              http/transit-body]
      ["/whoami" {:get whoami-data}]]]]])

