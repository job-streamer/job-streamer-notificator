(ns job-streamer.notificator.template
  (:import [com.github.jknack.handlebars Handlebars]
           [com.github.jknack.handlebars.io ClassPathTemplateLoader]))


(def handlebars (Handlebars.))

(defn render-inline [inline parameters]
  (let [template (.compileInline handlebars inline)]
    (.apply template parameters)))

(defn render [template-name parameters hbs-base-dir]
  (let [loader (.setPrefix (ClassPathTemplateLoader.) hbs-base-dir)
        handlebars-with-loader (apply #(Handlebars.) loader)
        template (.compile handlebars-with-loader template-name)]
    (.apply template parameters)))
