(ns serenity.dashboard.templates.profile
  (:require [kioo.om :as kioo]
            [om.core :as om :include-macros true]
            [om.dom :as dom]
            [cljs.core.async :as async])
  (:require-macros [kioo.om :refer  [deftemplate defsnippet]]
                   [cljs.core.async.macros :refer  [go]]))

(defn show
  "Set display css of current kioo selector"
  [bool]
  (kioo/set-style :display (if bool "block" "none")))

(deftemplate grantee-page "templates/profile.html"
  [app-state owner]
  {[:.badge] (kioo/content (:total-letters app-state))
   [:#input-error] (show false)})

