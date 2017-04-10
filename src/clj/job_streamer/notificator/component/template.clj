(ns job-streamer.notificator.component.template
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]])
  (:import [com.github.jknack.handlebars Handlebars]
           [com.github.jknack.handlebars.helper StringHelpers]
           [com.github.jknack.handlebars.io FileTemplateLoader CompositeTemplateLoader
            ClassPathTemplateLoader TemplateLoader]))


(defprotocol ITemplateEngine
  (render [this template-name parameters])
  (render-inline [this inline parameters]))


(defrecord HandlebarsEngine [prefix]
  component/Lifecycle

  (start [component]
    (if (:runtime component)
      component
      (let [loader (CompositeTemplateLoader.
                    (into-array TemplateLoader
                                [(FileTemplateLoader. prefix)
                                 (ClassPathTemplateLoader. prefix)]))
            runtime (.registerHelpers (Handlebars. loader) StringHelpers)]

        (assoc component :runtime runtime))))

  (stop [component]
    (dissoc component :runtime))

  ITemplateEngine
  (render [{:keys [runtime]} template-name parameters]
    (let [template (.compile runtime template-name)]
      (.apply template parameters)))

  (render-inline [{:keys [runtime]} inline parameters]
  (let [template (.compileInline runtime inline)]
    (.apply template parameters))))

(defn handlebars-engine [options]
  (map->HandlebarsEngine options))
