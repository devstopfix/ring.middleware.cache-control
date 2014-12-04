(ns ring.middleware.cache-control
  (:require [ring.util.response :as res]))

(def http-cache-control "Cache-Control")
(def http-expires "Expires")

(defn- has-cache-directives? [response]
  (or
    (get-in response [:headers http-cache-control])
    (get-in response [:headers http-expires])))

(defn- cache-control-max-age-handler [handler get-status-age]
  "Excutes the handler. The second argument is a function
   that accepts an HTTP status code (Integer) and returns
   max-age in seconds or nil."
  (fn [request]
    (let [response (handler request)
          status (get response :status 0)
          age (get-status-age status)]
      (if (or (has-cache-directives? response) (not age))
        response
        (res/header response http-cache-control (format "max-age=%d" age))))))

(defn cache-control-max-age [handler status-age]
  "Middleware excutes the handler and if the response does not
   have any Cache-Control or Expires directives, and the status code
   exists in the given status-age map, then a 'Cache-Control: max-age'
   header is added to the response with the duration given in the map.
   Second parameter is a map of HTTP status codes to age in seconds
   e.g. {200 60, 404 3600}"
  (cache-control-max-age-handler
    handler
    (partial get status-age)))

(defn perturb-fn []
  "Return a function that takes an integer and perturbs it
   based on a normal distribution."
  (let [rnd (java.util.Random.)]
    (fn [x]
      (when x
        (if (<= x 4)
          x
          (let [σ (* x 0.0625 (.nextGaussian rnd))]
            (+ x (int σ))))))))

(defn cache-control-max-age-perturbed [handler status-age]
  "Middleware excutes the handler and if the response does not
   have any Cache-Control or Expires directives, and the status code
   exists in the given status-age map, then a 'Cache-Control: max-age'
   header is added to the response with a duration calculated by a
   normal distribtion around the value given in the map.
   Second parameter is a map of HTTP status codes to age in seconds
   e.g. {200 60, 404 3600}"
  (cache-control-max-age-handler
    handler
    (comp (perturb-fn) (partial get status-age))))
