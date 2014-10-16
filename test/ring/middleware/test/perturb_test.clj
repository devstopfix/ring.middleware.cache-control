(ns ring.middleware.test.perturb-test
  (:require [clojure.test :refer :all]
            [ring.middleware.cache-control :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :as ct :refer (defspec)]))

; the output should be within +/- 30% of the input
(defspec test-perturb-variation-range
  1e6
  (let [perturb (perturb-fn)]
    (prop/for-all [a gen/s-pos-int]
      (let [pa (perturb a)]
        (and
          (>= pa (int (* a 0.6)))
          (<= pa (int (* a 1.4))))))))
