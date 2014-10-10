(ns ring.middleware.test.cache-control-test
  (:require [clojure.test :refer :all]
            [ring.middleware.cache-control :refer :all]
            [ring.mock.request :as mock]))

(defn- assert-max-age-cache-control
  ([response]
    (->>
      (get-in response [:headers "Cache-Control"] "")
      (re-matches #"max-age=(\d+)")
      (last)))
  ([response expected]
    (= expected
      (Integer/parseInt (assert-max-age-cache-control response)))))

(def app-200
  (->
    (fn [_] {:status 200 :headers {} :body "OK"})
    (cache-control-max-age)))

(deftest test-add-directives
  (testing "add max-age"
    (let [response (app-200 (mock/request :get ""))]
      (is
        (assert-max-age-cache-control response)))))

(def app-418
  (->
    (fn [_] {:status 418 :headers {} :body "Teapot"})
    (cache-control-max-age)))

(deftest test-do-not-add-directives
  (testing "do not add for unparameterized status code"
    (let [response (app-418 (mock/request :get ""))]
      (is
        (= {} (:headers response))))))

(def app-200-age
  (->
    (fn [_] {:status 200 :headers {"Cache-Control" "max-age=999"} :body "OK"})
    (cache-control-max-age)))

(def app-200-expires
  (->
    (fn [_] {:status 200 :headers {"Expires" "tomorrow"} :body "OK"})
    (cache-control-max-age)))

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
