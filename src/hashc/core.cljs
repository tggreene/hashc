(ns hashc.core
  (:require [clojure.string :as str]
            [zprint.core :as zprint])
  (:require-macros hashc.core))

(def location-style "color: #47E; font-weight: bold;")

(defn log
  "We double colon here to avoid url parsing in browser console"
  [{:keys [fn-symbol line orig-form result]}]
  (js/console.log
   (str "%c#c["
        fn-symbol
        "::"
        line
        "] "
        orig-form)
   location-style
   "=>"
   result))

(defn sanitize-ns-names
  [name]
  (-> name
      (str/replace #"_" "-")
       ;; reagent compiles components to it's own source tree under cmp
      (str/replace #"cmp\." "")))

(defn fn-symbol
  [ns]
  (let [stack (. (js/Error) -stack)
        ns-pattern (re-pattern (str/replace (str ns) #"\." "\\$"))
        probable-fn (->> stack
                         (re-seq #"at ([^ ]+) .*")
                         (map second)
                         (filter #(re-find ns-pattern %))
                         first)]
    (if probable-fn
      (let [parts (->> (str/split probable-fn #"\$")
                       (map sanitize-ns-names))
            fn-symbol (symbol (str/join "." (butlast parts)) (last parts))]
        fn-symbol)
      ;; if we can't find it assume it's repl eval
      'repl-eval)))
