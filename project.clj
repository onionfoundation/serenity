(defproject serenity "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.2.374"]

                 ;; Database
                 [com.datomic/datomic-free "0.9.5350"]
                 [io.rkn/conformity "0.4.0"]

                 ;; Server-side
                 [io.pedestal/pedestal.service "0.4.1"]
                 [io.pedestal/pedestal.jetty "0.4.1"]
                 [enlive "1.1.6"]
                 [environ "1.0.0"] ;; Config and Environment variable override
                 [geheimtur "0.3.0"] ;; Auth & Auth
                 [ns-tracker "0.3.0"] ;; Auto-reload server side on edits (without reval in REPL)

                 ;; Client-side
                 [org.clojure/clojurescript "1.8.40"]
                 [org.omcljs/om "0.9.0"]
                 [kioo "0.4.2" :exclusions [om]]
                 [cljs-http "0.1.40"]

                 ;; Logging
                 [ch.qos.logback/logback-classic "1.1.3" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.12"]
                 [org.slf4j/jcl-over-slf4j "1.7.12"]
                 [org.slf4j/log4j-over-slf4j "1.7.12"]

                 ;; Deps cleanup
                 [org.clojure/tools.reader "1.0.0-alpha3"]
                 [org.clojure/java.classpath "0.2.3"]
                 [commons-codec "1.10"]
                 [com.fasterxml.jackson.core/jackson-core "2.5.3"]]
  :min-lein-version "2.5.3"
  :resource-paths ["config" "resources"]
  ;:java-source-paths ["java"]
  ;:javac-options ["-target" "1.8" "-source" "1.8"]
  :global-vars  {;*warn-on-reflection* true
                 ;*unchecked-math* :warn-on-boxed
                 *assert* true}
  :pedantic? :abort
  :aliases {"build" ["uberjar"]}
  :profiles {:uberjar {:aot [serenity.service]}
             :prod {:aot [serenity.service]}
             :srepl {:jvm-opts ^:replace ["-d64" "-server"
                                          "-XX:+UseG1GC"
                                          "-Dclojure.server.repl={:port 5555 :accept clojure.core.server/repl}"]}
             :dev {:aliases {"dumbrepl" ["trampoline" "run" "-m" "clojure.main/main"]
                             "srepl" ["with-profile" "srepl" "trampoline" "run" "-m" "clojure.main/main"]
                             "run-dev" ["trampoline" "run" "-m" "serenity.service/run-dev"]
                             "build-all" ["do" "clean," "check," "cljsbuild" "once"]
                             "test-all" ["do" "clean," "test," "cljsbuild" "test,"] }
                   :dependencies [[criterium "0.4.4"]
                                  [thunknyc/profile "0.5.2"]
                                  [org.clojure/tools.trace "0.7.9"]
                                  [org.clojure/tools.namespace "0.3.0-alpha3"]
                                  [org.clojure/test.check "0.9.0"]]
                   :codox {:output-path "docs/api-docs"}
                   :plugins [[lein-figwheel "0.5.2"]
                             [lein-cljsbuild "1.1.3" :exclusions [[org.clojure/clojure]]]
                             [lein-marginalia "0.8.0" :exclusions [[org.clojure/clojure]
                                                                   ;; Use the tools.reader from `cljfmt`
                                                                   [org.clojure/tools.reader]]]
                             [lein-codox "0.9.0" :exclusions [[org.clojure/clojure]]]
                             ;; Requires lein 2.5.0 or higher
                             [lein-cljfmt "0.3.0" :exclusions [[org.clojure/clojure]]]]}}
  :main ^{:skip-aot true} serenity.service
  :jvm-opts ^:replace ["-d64" "-server"
                         ;"-Xms1g" ;"-Xmx1g"
                         ;"-XX:+UnlockCommercialFeatures" ;"-XX:+FlightRecorder"
                         ;"-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8030"
                         "-XX:+UseG1GC"
                         ;"-XX:+UseConcMarkSweepGC" "-XX:+UseParNewGC" "-XX:+CMSParallelRemarkEnabled"
                         ;"-XX:+ExplicitGCInvokesConcurrent"
                         "-XX:+AggressiveOpts"
                         ;-XX:+UseLargePages
                         "-XX:+UseCompressedOops"]
  :cljsbuild {;:test-commands {"test" ["phantomjs"
              ;                        "test/resources/phantom/unit-test.js"
              ;                        "test/resources/phantom/unit-test.html"]}
              :builds [{:id "dev"
                        :source-paths ["src/serenity/dashboard"]
                        :compiler {:output-to "resources/public/dev/js/app.js"
                                   :output-dir "resources/public/dev/js/out"
                                   :optimizations :none
                                   :main serenity.dashboard.main
                                   :asset-path "js/out"
                                   :source-map true
                                   :pretty-print true
                                   :cache-analysis true}
                        :figwheel true}
                       {:id "test"
                        :source-paths ["src/serenity/dashboard" "test"]
                        :compiler {:output-dir "resources/public/dev/test/js/out"
                                   :output-to "resources/public/dev/test/js/app.js"
                                   :source-map "resources/public/dev/test/js/app.js.map"
                                   :optimizations :whitespace
                                   :pretty-print true
                                   :cache-analysis true
                                   :static-fns true
                                   ;:preamble ["react/react.min.js"
                                   ;           "preamble/head.min.js"
                                   ;           "preamble/reveal.min.js"]
                                   }}
                       {:id "prod"
                        :source-paths ["src/serenity/dashboard"]
                        :compiler {:output-to "resources/public/js/app.js"
                                   :optimizations :advanced
                                   :pretty-print false
                                   ;:elide-asserts true
                                   :cache-analysis true
                                   :pseudo-names true
                                   :static-fns true
                                   ;:preamble ["react/react.min.js"
                                   ;           "preamble/head.min.js"
                                   ;           "preamble/reveal.min.js"]
                                   ;:externs ["preamble/reveal.min.js"]
                                   :closure-warnings {:externs-validation :off
                                                      :non-standard-jsdoc :off}}}]})

