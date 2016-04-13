(ns serenity.routes-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [serenity.test-helpers :as helpers]))

(deftest home-page-test
  (is (=
       (:body (response-for (helpers/service) :get "/"))
       "Hello, from Serenity!"))
  (is (=
       (:headers (response-for (helpers/service) :get "/"))
       {"Content-Type" "text/plain"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"})))

