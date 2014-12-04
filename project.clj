(defproject ring.middleware.cache-control "1.1"
  :description "Ring middleware for cache control of resources in a REST architecture."
  :url "https://github.com/devstopfix/ring.middleware.cache-control"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-core "1.3.2"]
                 [ring-mock "0.1.5"]]
  :profiles {
    :test {
       :dependencies [[org.clojure/test.check "0.6.1"]]}})


