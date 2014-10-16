# Histograms

The histograms can be generated with [Incanter](https://github.com/incanter/incanter).

    [incanter "1.5.5"]
    
Script:

```clojure
(require '[ring.middleware.cache-control :as cc])
(use '(incanter core stats charts))

(defn sample-perturb [x]
  (let [p (cc/perturb-fn)]
    (repeatedly #(p x))))

(view
  (histogram
    (take 10000 (sample-perturb (* 60 60)))))
```

