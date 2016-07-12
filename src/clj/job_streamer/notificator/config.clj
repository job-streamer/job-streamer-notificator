(ns job-streamer.notificator.config
  (:require [environ.core :refer [env]]))

(def defaults
  {:camel {:port 2121
           :template-dir "templates"}})

(def environ
  {:camel {:port (some-> env :notificator-port Integer.)
           :config-path (some-> env :notificator-config)
           :template-dir (some-> env :notificator-template-dir)}})
