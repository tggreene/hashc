# hashc

Shameless plaigarism of [hashp](https://github.com/weavejester/hashp) for
console.log in cljs:

```clojure
(ns example.core)

(defn mean [xs]
  (/ (double #c (reduce + xs)) #c (count xs)))
```

Evaluating the form:

```clojure
(mean (range 10)) ;; => 4.5
```

Browser console:

    #c[hashc.core/fn-symbol::2] (reduce + xs) => 45
    #c[hashc.core/fn-symbol::2] (count xs) => 10

The primary difference being that the output will use cljs devtools formatters
if available (automatically added by shadow-cljs) to print the resulting value.

It doesn't currently print output to stdout, only to the browser console.

<!-- ## Installing -->

<!-- You can add it with shadow-cljs dependencies as below: -->

<!--     {:dependencies [.../hashc "0.0.1"] -->
<!--      :builds {:app {:devtools {:preloads [hashc.core]}}}} -->

<!-- Or alternatively via ~/.shadow-cljs/config.edn and --config-merge: -->

<!--     ~/.shadow-cljs/config.edn: -->

<!-- {:dependencies [[.../hashc "0.0.1"]]} -->
<!-- Run: -->

<!-- shadow-cljs watch app --config-merge '{:devtools {:preloads [hashc.core]}}' -->

<!-- If you use deps with shadow-cljs you'll likely need to add it to the project -->
<!-- deps or to your home directory deps as an alias. -->
