(ns ring.middleware.cache-control
  (:require [ring.util.response :as res]))

(def default-ages
  "Map of HTTP Status Code to max-age in seconds"
  {200 (* 60 60)
   400 (* 60 60 24)
   404 (* 60 6)
   410 (* 60 60 24)})

(def http-cache-control "Cache-Control")
(def http-expires "Expires")

(defn- has-cache-directives? [response]
  (or
    (get-in response [:headers http-cache-control])
    (get-in response [:headers http-expires])))

; TODO allow merging and overriding of default age map
(defn cache-control-max-age [handler]
  (fn [request]
    (let [response (handler request)
          status (get response :status 0)
          age (get default-ages status)]
      (if (or (has-cache-directives? response) (not age))
        response
        (res/header response http-cache-control (format "max-age=%d" age))))))
