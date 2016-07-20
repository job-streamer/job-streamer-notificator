(ns job-streamer.notificator.component.camel-test
  (:require [com.stuartsierra.component :as component]
            [meta-merge.core :refer [meta-merge]]
            [clojure.test :refer :all]
            [clojure.pprint :refer :all]
            [clojure.edn :as edn]

            [org.httpkit.client :as http]
            [shrubbery.core :refer :all]

            (job-streamer.notificator.component [camel :refer [camel-server]]
                                                [template :refer [ITemplateEngine]])
            (job-streamer.notificator [system :as system]
                                      [config :as config])))

(def test-config
  {:camel {:port 21212}
   :template {:prefix "dev-resources"}})

(def config
  (meta-merge config/defaults
              config/environ
              test-config))

(defn template-mock []
  (spy (reify ITemplateEngine
         (render [this template-name parameters]
           )
         (render-inline [this inline parameters]
           ))))

(defn new-system [config]
  (-> (component/system-map
       :camel   (camel-server (:camel config))
       :template (template-mock))
      (component/system-using
       {:camel [:template]})
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
