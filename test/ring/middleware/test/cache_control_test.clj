(ns ring.middleware.test.cache-control-test
  (:require [clojure.test :refer :all]
            [ring.middleware.cache-control :refer :all]
            [ring.mock.request :as mock]))

(defn- assert-max-age-cache-control
  "Assert we have a Cache-Control header, and optionally
   that it has a certain max-age value."
  ([response]
    (->>
      (get-in response [:headers "Cache-Control"] "")
      (re-matches #"max-age=(\d+)")
      (last)))
  ([response expected-maxage]
    (= expected-maxage
      (Integer/parseInt (assert-max-age-cache-control response)))))

(def app-200
  (->
    (fn [_] {:status 200 :headers {} :body "OK"})
    (cache-control-max-age {200 (* 60 60)})))

(deftest test-add-directives
  (testing "add max-age"
    (let [response (app-200 (mock/request :get ""))]
      (is
        (assert-max-age-cache-control response 3600)))))

(def app-418
  (->
    (fn [_] {:status 418 :headers {} :body "Teapot"})
    (cache-control-max-age {400 0})))

(deftest test-do-not-add-directives
  (testing "do not add for unparameterized status code"
    (let [response (app-418 (mock/request :get ""))]
      (is
        (= {} (:headers response))))))

(def app-200-age
  (->
    (fn [_] {:status 200 :headers {"Cache-Control" "max-age=999"} :body "OK"})
    (cache-control-max-age {200 360})))

(def app-200-expires
  (->
    (fn [_] {:status 200 :headers {"Expires" "tomorrow"} :body "OK"})
    (cache-control-max-age {200 360})))

(deftest test-pass-through
  (testing "don't add if we have max-age"
    (let [response (app-200-age (mock/request :get ""))]
      (is
        (assert-max-age-cache-control response 999))))

  (testing "don't add if we have Expires"
    (let [response (app-200-expires (mock/request :get ""))]
      (is
        (not
          (assert-max-age-cache-control response ))))))


; Test that our standard age is varied
; Note there is a very rare change this case
; will fail if the age is perturbed by zero.
; In this case just re-run...

(def app-200-perturbed
  (->
    (fn [_] {:status 200 :headers {} :body "OK"})
    (cache-control-max-age-perturbed {200 31536000})))

(deftest test-perturbed-age
  (testing "add max-age with purterbed value"
    (let [response (app-200-perturbed (mock/request :get ""))
          age      (Integer/parseInt
                     (assert-max-age-cache-control response))]
      (is
        (>=   age 22075200))
      (is
        (not= age 31536000))
      (is
        (<=   age 40996800)))))

