ring.middleware.cache-control
=============================

[Ring][2] middleware for cache control of [resources][1].

## Installation

Check out this project and run:

   lein install

Then add the following dependency to your `project.clj`:

    [ring.middleware.cache-control "0.1.0"]

## Documentation

This middleware appends a [Cache-Control][3] [max-age][4] directive 
to the response. The age is expressed in seconds and parameterized
via a map of status code to age.

TODO - the map is hard-coded in v0.1.0

## Using

```clojure
(use '[ring.middleware.cache-control :only [cache-control-max-age]])

(def app
  (-> app-routes
    (cache-control-max-age)
    ...))
```

appends this header for a 200 response:

```
Cache-Control:max-age=3600
```

## License

Copyright © 2014 J Every

Distributed under the MIT License, the same as [Ring][2].

[1]: http://en.wikipedia.org/wiki/Resource-oriented_architecture
[2]: https://github.com/ring-clojure/ring
[3]: http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9
[4]: http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3
