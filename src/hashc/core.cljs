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

(defn sanitize-stack-element
  [s]
  (let [segments (str/split s #"\$")]
    (if (= 1 (count segments))
      s
      (let [s' (str (str/join "." (butlast segments))
                    "/"
                    (last segments))]
        (-> s'
            (str/replace #"^Object\." "")
            (str/replace #"_GT_" ">")
            (str/replace #"_" "-"))))))

(def stack-translations
  "If we receive one of the following as the final stack element we can
  translate to something a little clearer"
  {"shadow.cljs.devtools.client.browser/global-eval"
   "repl-eval"

   "re-frame.std-interceptors.fx-handler->interceptor-/-fx-handler-before"
   "re-frame-anonymous-fx-handler"})

(comment
  (map sanitize-stack-element
       ["hashc$core$fn_symbol"
        "excel$frontend$pages$schedule$events$something"
        "eval"
        "eval"
        "Object.shadow$cljs$devtools$client$browser$global_eval"
        "WebSocket.eval"]))

(def example-repl-eval
  ["hashc$core$fn_symbol"
   "excel$frontend$pages$schedule$events$something"
   "eval"
   "eval"
   "Object.shadow$cljs$devtools$client$browser$global_eval"
   "Object.eval"
   "Object.shadow$cljs$devtools$client$shared$do_invoke"
   "Object.shadow$cljs$devtools$client$shared$handle_repl_invoke"
   "eval"
   "Object.shadow$cljs$devtools$client$shared$interpret_action"
   "Object.shadow$cljs$devtools$client$shared$interpret_actions"
   "eval"
   "Object.shadow$remote$runtime$shared$process"
   "Object.eval"
   "Object.shadow$cljs$devtools$client$shared$remote_msg"
   "WebSocket.eval"])

(def example-re-frame-eval
  ["Object.hashc$core$fn_symbol"
   "eval"
   "eval"
   "re_frame$std_interceptors$fx_handler__GT_interceptor_$_fx_handler_before"
   "Object.re_frame$interceptor$invoke_interceptor_fn"
   "Object.re_frame$interceptor$invoke_interceptors"
   "Object.re_frame$interceptor$execute"
   "Object.re_frame$events$handle"
   "Object.eval"
   "Object.eval"
   "eval"
   "Object.eval"
   "G__125578"
   "MessagePort.channel.port1.onmessage"])

(comment
  (map sanitize-stack-element example-re-frame-eval)
  )

(defn fn-symbol
  [ns]
  (let [stack (. (js/Error) -stack)
        probable-fn (->> stack
                         (re-seq #"at ([^ ]+) .*")
                         (map (comp sanitize-stack-element second))
                         (drop 1)
                         (remove #(= "eval" %))
                         first)]
    (or probable-fn
        'repl-eval)))
