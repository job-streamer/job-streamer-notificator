(ns job-streamer.notificator.main
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [duct.util.runtime :refer [add-shutdown-hook]]
            [meta-merge.core :refer [meta-merge]]
            (job-streamer.notificator [config :as config]
                                      [system :refer [new-system]])))

(def prod-config {})

(def config
  (meta-merge config/defaults
              config/environ
              prod-config))

(def banner "
   ___       _     _____ _
  |_  |     | |   /  ___| |                         Notificator
    | | ___ | |__ \\ `--.| |_ _ __ ___  __ _ _ __ ___   ___ _ __
    | |/ _ \\| '_ \\ `--. \\ __| '__/ _ \\/ _` | '_ ` _ \\ /_ \\ '__|
/\\__/ / (_) | |_) /\\__/ / |_| | |  __/(_| | | | | | |  __/ |
\\____/ \\___/|_.__/\\____/ \\__|_|  \\___|\\__,_|_| |_| |_|\\___|_|
  ")

(defn -main [& args]
  (when (empty? args)
    (.println *err* "Usage: bin/notificator [rule file] [templates dir]")
    (.exit (Runtime/getRuntime) 255))

  (let [args-config (meta-merge {}
                                (when-let [rules-path (first args)]
                                  {:camel {:rules-path rules-path}})
                                (when-let [templates-dir (second args)]
                                  {:camel {:templates-dir templates-dir}}))
        system (new-system (meta-merge config args-config))]
    (println banner)
    (add-shutdown-hook ::stop-system #(component/stop system))
    (component/start system)))
