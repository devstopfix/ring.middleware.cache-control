(ns ring.middleware.test.behaviour-test
  (:require [clojure.test :refer :all]
            [ring.middleware.cache-control :refer :all]
            [ring.mock.request :as mock]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :as ct :refer (defspec)]))

;
; Middleware parameterization generator
;

(defn range-inclusive [start end]
  "Return a lazy-seq as per range, but inclusive of end value."
  (range start (inc end)))

(def common-http-status-codes
  "A set of common HTTP status codes"
  (reduce into []
          (list
            (range-inclusive 100 103)
            (range-inclusive 200 208)
            (range-inclusive 300 208)
            (range-inclusive 400 418)
            (range-inclusive 500 504))))

(defn sample-status-age-map []
  "Make a generator: a map of http status to age in seconds.
   NB gen/elements picks a single HTTP status code
   from the vector above."
  (gen/map
    (gen/elements common-http-status-codes)
    gen/pos-int))

; Helper functions to check age

(defn age [hdr]
  "Extract age from Cache-Control header.
   'max-age=57' -> 57"
  (Long/parseLong (last (.split hdr "="))))

(defn within-range [normal x]
  "Test that x is within 40% of x"
  (let [range 0.4]
    (and
      (>= x (int (* normal (- 1.0 range))))
      (<= x (int (* normal (+ 1.0 range)))))))

;
; Generate a sample cache control map for each test.
; If the control map happens to contain 200 (which is always the result
; of our test) then see if the age was perturbed.
; Otherwise no cache control headers should have been added.
;

(defn app-200 [status-age-map]
  "Returns a handler that returns a fixed response."
  (->
    (fn [_] {:status 200 :headers {} :body "OK"})
    (cache-control-max-age-perturbed status-age-map)))

(defspec test-behaviour-of-middleware-perturb
         10000
         (prop/for-all [status-age-map (sample-status-age-map)]
                       (let [handler (app-200 status-age-map)
                             response (handler (mock/request :get ""))]
                         (if (contains? status-age-map 200)
                           (let [age-param (get status-age-map 200)
                                 age-actual (age (get-in response [:headers "Cache-Control"]))]
                             (if (<= age-param 4)
                               (is (= age-param age-actual)
                                   "For small ages (4 seconds or less) no change is made")
                               (is (within-range age-param age-actual)
                                   (str "Age " age-actual " is not within 40% of " age-param))))
                           (is (= {} (get response :headers))
                               "No cache directives expected")))))


;
; Test that responses containing cache directives are never changed
;

(defn app-200-age [status-age-map]
  "Returns a handler that returns a fixed response."
  (->
    (fn [_] {:status 200 :headers {"Cache-Control" "max-age=999"} :body "OK"})
    (cache-control-max-age-perturbed status-age-map)))

(defspec test-behaviour-of-middleware-pass-through-response
         10000
         (prop/for-all [status-age-map (sample-status-age-map)]
                       (let [handler (app-200-age status-age-map)
                             response (handler (mock/request :get ""))]
                         (let [age-actual (age (get-in response [:headers "Cache-Control"]))]
                           (is (= 999 age-actual)
                               "Response should not have been changed")))))
