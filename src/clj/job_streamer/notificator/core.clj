(ns job-streamer.notificator.core
  (:require [clojure.edn :as edn]
            [job-streamer.notificator.template :as template])
  (:use [clojure.walk :only [stringify-keys]]
        [environ.core :only [env]])
  (:import [org.apache.camel Processor]
           [org.apache.camel.impl DefaultCamelContext]
           [org.apache.camel.builder Builder RouteBuilder ValueBuilder])
  (:gen-class))

(def edn-processor
  (proxy [Processor] []
    (process [exchange]
      (let [in  (.getIn exchange)
            out (.getOut exchange)]
        (.setBody out (edn/read-string (.getBody in String)))
        (.setHeader out "type" (.substring (.getHeader in "CamelHttpUri") 1))))))

(defn make-template-processor [template-name]
  (proxy [Processor] []
    (process [exchange]
      (let [parameters  (.getBody (.getIn exchange))
            out (.getOut exchange)]
        (.setHeader out "to" (:to parameters))
        (.setHeader out "username" (:username parameters))
        (.setHeader out "password" (:password parameters))
        (.setBody out (template/render template-name (stringify-keys parameters)))))))

(defn process-template [route config]
  (if-let [template-name (:message-template config)]
    (.process route (make-template-processor template-name))
    (if-let [message (:message config)]
      (.process route (proxy [Processor] []
                        (process [exchange]
                          (.setBody (.getOut exchange) message))))
      route)))

(defn filter-rules [rules]
  [(filter (fn [[type config]] (find config :to)) rules)
   (filter (fn [[type config]] (not (find config :to))) rules)])

(defn -main [& args]
  (let [rules   (load-file (first args))
        context (DefaultCamelContext.)
        port (get env :port 2121)
        [consumer-rules producer-rules] (filter-rules rules)]
    (.addRoutes context
                (proxy [RouteBuilder] []
                  (configure []
                    (let [route (.. this
                                    (from (str "jetty:http://0.0.0.0:" port "/?matchOnUriPrefix=true"))
                                    (process edn-processor)
                                    choice)]
                      (doseq [[type config] producer-rules]
                        (let [conditional-route (.when route (. (Builder/header "type") (isEqualTo (name type))))]
                          (-> conditional-route
                              (process-template config)
                              (.recipientList (Builder/simple (:uri config))))))
                      (-> route
                          (.otherwise)
                          (.to "log:ROUTE_NOT_FOUND?showAll=true")))
                    (doseq [[type config] consumer-rules]
                      (let [route (.. this
                                      (from (:uri config)))]
                        (process-template route config)
                        (.to route (:to  config)))))))
    (.start context)))
