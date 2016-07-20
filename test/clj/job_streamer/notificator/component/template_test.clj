(ns job-streamer.notificator.component.template-test
  (:require [com.stuartsierra.component :as component]
            [meta-merge.core :refer [meta-merge]]
            [clojure.test :refer :all]
            [clojure.pprint :refer :all]

            (job-streamer.notificator.component [template :refer [handlebars-engine render]])
            (job-streamer.notificator [system :as system]
                                      [config :as config])))

(def test-config
  {:camel {:port 21212}
   :template {:prefix "dev-resources"}})

(def config
  (meta-merge config/defaults
              config/environ
              test-config))

(defn new-system [config]
  (-> (component/system-map
       :template (handlebars-engine (:template config)))
      (component/system-using
       {})
      (component/start-system)))

(deftest rendering
  (let [system (new-system config)]
    (testing "using template"
      (is (= "Hello, kawasima!\n"
             (render (:template system) "success" {"message" "kawasima"}))) )))
