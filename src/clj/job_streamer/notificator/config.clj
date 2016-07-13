(ns job-streamer.notificator.config
  (:require [environ.core :refer [env]]))

(def defaults
  {:camel {:port 2121
           :templates-dir "templates"}})

(def environ
  {:camel {:port (some-> env :notificator-port Integer.)
           :rules-path (some-> env :notificator-rules)
           :templates-dir (some-> env :notificator-templates-dir)}})
