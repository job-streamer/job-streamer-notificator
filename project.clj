(defproject net.unit8.jobstreamer/job-streamer-notificator (clojure.string/trim-newline (slurp "VERSION"))
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.stuartsierra/component "0.3.1"]
                 [duct "0.8.0"]
                 [meta-merge "1.0.0"]

                 [environ "1.0.3"]
                 [ch.qos.logback/logback-classic "1.1.7"]
                 [org.apache.camel/camel-core  "2.17.2"]
                 [org.apache.camel/camel-http  "2.17.2"]
                 [org.apache.camel/camel-mail  "2.17.2"]
                 [org.apache.camel/camel-jetty "2.17.2"]
                 [com.github.jknack/handlebars "4.0.5"]]

  :source-paths ["src/clj"]
  :test-paths   ["test/clj"]
  :source-java-paths ["src/java"]
  :javac-options ["-target" "1.7" "-source" "1.7" "-Xlint:-options"]
  :prep-tasks [["javac"] ["compile"]]

  :main ^:skip-aot job-streamer.notificator.main
  :pom-plugins [[org.apache.maven.plugins/maven-assembly-plugin "2.5.5"
                 {:configuration [:descriptors [:descriptor "src/assembly/dist.xml"]]}]
                [org.apache.maven.plugins/maven-compiler-plugin "3.3"
                 {:configuration ([:source "1.7"] [:target "1.7"]) }]]

  :profiles
  {:dev  [:project/dev  :profiles/dev]
   :test [:project/test :profiles/test]
   :uberjar {:aot :all}
   :profiles/dev  {}
   :profiles/test {}
   :project/dev   {:dependencies [[duct/generate "0.8.0"]
                                  [reloaded.repl "0.2.2"]
                                  [org.clojure/tools.namespace "0.2.11"]
                                  [org.clojure/tools.nrepl "0.2.12"]
                                  [com.gearswithingears/shrubbery "0.3.1"]
                                  [http-kit "2.1.18"]
                                  [eftest "0.1.1"]
                                  [kerodon "0.8.0"]]
                   :source-paths ["dev"]
                   :repl-options {:init-ns user}
                   :env {:port "45102"}}
   :project/test {:dependencies [[junit "4.12"]]}})
