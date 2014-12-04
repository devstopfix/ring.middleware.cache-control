ring.middleware.cache-control
=============================

[Ring][2] middleware for cache control of [resources][1].

## Installation

Check out this project and run:

    lein install

Then add the following dependency to your `project.clj`:

    [ring.middleware.cache-control "1.1"]

## Documentation

This middleware appends a [Cache-Control][3] [max-age][4] directive 
to the response. The age is expressed in seconds and parameterized
via a map of status code to age.

### Add the middleware to your App

```clojure
(use '[ring.middleware.cache-control :only [cache-control-max-age]])

(def app
  (-> app-routes
    (cache-control-max-age {200 (* 60 60))
    ...))
```

appends 1 one-hour header for a 200 response:

```
...
Cache-Control:max-age=3600
```

### Distribute the load

There is a second middleware provided called ```cache-control-max-age-perturbed``` 
 which applies a normal-distribution to the cache age parameter value. It
 can help spread the load on a heavily utilized resource.
 
These histograms show the variation for 1 minutes, 1 hour and 1 day:

![1 minute](https://github.com/devstopfix/ring.middleware.cache-control/raw/master/docs/1-minute.png "1 Minute")

![1 hour](https://github.com/devstopfix/ring.middleware.cache-control/raw/master/docs/1-hour.png "1 Hour")

![1 day](https://github.com/devstopfix/ring.middleware.cache-control/raw/master/docs/24-hours.png "1 Day")

[Source code of histograms](docs/histograms.md)

# License

Copyright © 2014 J Every

Distributed under the MIT License, the same as [Ring][2].

[1]: http://en.wikipedia.org/wiki/Resource-oriented_architecture
[2]: https://github.com/ring-clojure/ring
[3]: http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9
[4]: http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3
