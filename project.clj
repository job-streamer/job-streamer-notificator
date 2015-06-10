(defproject job-streamer-notificator "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [environ "1.0.0"]
                 [ch.qos.logback/logback-classic "1.1.3"]
                 [org.apache.camel/camel-core  "2.15.2"]
                 [org.apache.camel/camel-mail  "2.15.2"]
                 [org.apache.camel/camel-jetty "2.15.2"]
                 [com.github.jknack/handlebars "2.1.0"]]
  :source-paths ["src/clj"]
  :source-java-paths ["src/java"]
  :main job-streamer.notificator.core)
