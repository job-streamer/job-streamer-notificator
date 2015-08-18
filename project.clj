(defproject job-streamer-notificator (clojure.string/trim-newline (slurp "VERSION"))
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [environ "1.0.0"]
                 [ch.qos.logback/logback-classic "1.1.3"]
                 [org.apache.camel/camel-core  "2.15.2"]
                 [org.apache.camel/camel-mail  "2.15.2"]
                 [org.apache.camel/camel-jetty "2.15.2"]
                 [com.github.jknack/handlebars "2.2.3"]]
  :source-paths ["src/clj"]
  :source-java-paths ["src/java"]
  :javac-options ["-target" "1.7" "-source" "1.7" "-Xlint:-options"]

  :aot :all
  :main job-streamer.notificator.core
  :pom-plugins [[org.apache.maven.plugins/maven-assembly-plugin "2.5.5"
                 {:configuration [:descriptors [:descriptor "src/assembly/dist.xml"]]}]]

  :profiles {:local {:resource-paths ["local-resources"]}})
