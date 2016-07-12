(ns job-streamer.notificator.system
  (:require [com.stuartsierra.component :as component]
            [duct.component.endpoint :refer [endpoint-component]]
            [duct.component.handler :refer [handler-component]]
            [duct.middleware.not-found :refer [wrap-not-found]]
            [meta-merge.core :refer [meta-merge]]
            (job-streamer.notificator.component [camel :refer [camel-server]])))

(def base-config
  {:camel {:port 2121}})

(defn new-system [config]
  (let [config (meta-merge base-config config)]
    (-> (component/system-map
         :camel (camel-server (:camel config)))
        (component/system-using
         {}))))
