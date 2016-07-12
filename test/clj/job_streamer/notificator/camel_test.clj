(ns job-streamer.notificator.camel-test
  (:require [com.stuartsierra.component :as component]
            [meta-merge.core :refer [meta-merge]]
            [clojure.test :refer :all]
            [clojure.pprint :refer :all]
            [clojure.edn :as edn]

            [org.httpkit.client :as http]

            (job-streamer.notificator.component [camel :refer [camel-server]])
            (job-streamer.notificator [system :as system]
                                      [config :as config])))

(def test-config
  {:camel {:port 21212
           :template-dir "dev-resources"}})

(def config
  (meta-merge config/defaults
              config/environ
              test-config))

(defn new-system [config]
  (-> (component/system-map
       :camel   (camel-server (:camel config)))
      (component/system-using {})
      (component/start-system)))

(deftest rules-are-working
  (let [rules {:test1 {:uri "mock:foo"
                       :message-template "success"}}
        system (new-system (assoc-in config [:camel :rules] rules))]
    (testing "mock"
      (let [endpoint (-> system :camel :camel-context
                         (.getEndpoint "mock:foo"))]
        (doto endpoint
          (.setResultWaitTime 1000)
          (.expectedMessageCount 1))
        @(http/get "http://localhost:21212/test1")
        (-> system :camel :camel-context
               (.getEndpoint "mock:foo")
               (.assertIsSatisfied))))
    (component/stop-system system)))
