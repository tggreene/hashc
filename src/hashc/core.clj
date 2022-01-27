(ns hashc.core
  (:require [clojure.walk :as walk]
            [zprint.core :as zprint]))

(def result-sym (gensym "result"))

(defn- hide-c-form [form]
  (if (and (seq? form)
           (vector? (second form))
           (= (-> form second first) result-sym))
    (-> form second second)
    form))

(defn c*
  [form]
  (let [orig-form (walk/postwalk hide-c-form form)
        {:keys [line]} (meta form)
        ns (str *ns*)]
    `(let [~result-sym ~form]
       (do (log {:fn-symbol (fn-symbol ~ns)
                 :line ~line
                 :orig-form ~(zprint/zprint-str orig-form {:color? true})
                 :result ~result-sym})
           ~result-sym))))
