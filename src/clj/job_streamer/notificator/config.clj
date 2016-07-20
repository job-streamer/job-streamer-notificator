(ns job-streamer.notificator.config
  (:require [environ.core :refer [env]]))

(def defaults
  {:camel {:port 2121}
   :template {:prefix "templates"}})

(def environ
  {:camel {:port (some-> env :notificator-port Integer.)
           :rules-path (some-> env :notificator-rules)}
   :template {:prefix  (some-> env :notificator-templates-prefix)}})
