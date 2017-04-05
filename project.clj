(defproject api-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-json "0.3.1"]
                 [clj-time "0.13.0"]
                 [clj-http "2.3.0"]
                 [clj-soup/clojure-soup "0.1.3"]
                 [org.apache.commons/commons-lang3 "3.1"]
                 [org.clojure/tools.logging "0.3.1"]
                 [ring/ring-defaults "0.2.1"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler api-test.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
