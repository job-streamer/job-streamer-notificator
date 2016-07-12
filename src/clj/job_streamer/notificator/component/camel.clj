(ns job-streamer.notificator.component.camel
  (:require [clojure.edn :as edn]
            [clojure.walk :refer [stringify-keys]]
            [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [job-streamer.notificator.template :as template])
  (:import [org.apache.camel Processor]
           [org.apache.camel.impl DefaultCamelContext]
           [org.apache.camel.builder Builder RouteBuilder ValueBuilder]))

(def edn-processor
  (proxy [Processor] []
    (process [exchange]
      (let [in  (.getIn exchange)
            out (.getOut exchange)]
        (.setBody out (edn/read-string (.getBody in String)))
        (.setHeader out "type" (.substring (.getHeader in "CamelHttpUri") 1))))))

(defn make-template-processor [template-name hbs-base-dir]
  (proxy [Processor] []
    (process [exchange]
      (let [parameters  (.getBody (.getIn exchange))
            out (.getOut exchange)]
        (.setHeader out "job-name" (:job-name parameters))
        (.setBody out (template/render template-name (stringify-keys parameters) hbs-base-dir))))))

(defn process-template [route config hbs-base-dir]
  (if-let [template-name (:message-template config)]
    (.process route (make-template-processor template-name hbs-base-dir))
    (if-let [message (:message config)]
      (.process route (proxy [Processor] []
                        (process [exchange]
                          (.setBody (.getOut exchange) message))))
      route)))

(defn filter-rules [rules]
  [(filter (fn [[type config]] (find config :to)) rules)
   (filter (fn [[type config]] (not (find config :to))) rules)])

(defrecord CamelServer [port rules templates-dir]
  component/Lifecycle

  (start [component]
    (let [context (DefaultCamelContext.)
          [consumer-rules producer-rules] (filter-rules rules)]
      (. context addRoutes
       (proxy [RouteBuilder] []
         (configure []
           (let [route (.. this
                           (from (str "jetty:http://0.0.0.0:" port "/?matchOnUriPrefix=true"))
                           (process edn-processor)
                           choice)]
             (doseq [[type config] producer-rules]
               (let [conditional-route (.when route (. (Builder/header "type") (isEqualTo (name type))))]
                 (-> conditional-route
                     (process-template config templates-dir)
                     (.recipientList (Builder/simple (:uri config))))))
             (-> route
                 (.otherwise)
                 (.to "log:ROUTE_NOT_FOUND?showAll=true")))
           (doseq [[type config] consumer-rules]
             (let [route (.. this
                             (from (:uri config)))]
               (process-template route config templates-dir)
               (.to route (:to  config)))))))
      (.start context)
      (assoc component :camel-context context)))

  (stop [component]
    (when-let [context (:camel-context component)]
      (.shutdown context))
    (dissoc component :camel-context)))

(defn camel-server [options]
  (map->CamelServer
   (if-let [rules-path (:rules-path options)]
     (assoc options :rules (load-file rules-path))
     options)))
