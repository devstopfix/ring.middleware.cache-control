(ns ring.middleware.cache-control
  (:require [ring.util.response :as res]))


(def http-cache-control "Cache-Control")
(def http-expires "Expires")

(defn- has-cache-directives? [response]
  (or
    (get-in response [:headers http-cache-control])
    (get-in response [:headers http-expires])))

(defn cache-control-max-age [handler status-age]
  "Middleware excutes the handler then looks up the http status-code
   in the status-age Map - if a value is found then it is added
   as a 'Cache-Control: max-age' header to the response."
  (fn [request]
    (let [response (handler request)
          status (get response :status 0)
          age (get status-age status)]
      (if (or (has-cache-directives? response) (not age))
        response
        (res/header response http-cache-control (format "max-age=%d" age))))))


(def rnd (java.util.Random.))

(->>
  (repeatedly #(.nextGaussian rnd))
  (take 10)
  (map #(* 100 %))
  (map int))
