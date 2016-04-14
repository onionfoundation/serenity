(ns serenity.dashboard.render
  (:require [om.core :as om :include-macros true]))

;; Utilities
;; ---------
(defn elem-by-id [dom-id]
  (.getElementById js/document dom-id))

;; Om specific
;; --------------
(defn init-renderer! [app-state app-dom-target main-template]
  (let [init-component (fn [data owner] (om/component (main-template data owner)))]
    (om/root init-component app-state {:target app-dom-target})))

