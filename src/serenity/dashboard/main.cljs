(ns serenity.dashboard.main
  (:require [serenity.dashboard.render :as render]
            [serenity.dashboard.templates.profile :as profile]))


(def app-target (render/elem-by-id "main"))
(def main-template profile/grantee-page)
(def initial-app-state
  {:total-letters 10})
(def app-state (atom initial-app-state))

;; Activate Om and turn our application "on"
(render/init-renderer! app-state app-target main-template)

(defn -main []
  (.log js/console "Hello World"))

(-main)

