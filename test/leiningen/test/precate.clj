(ns leiningen.test.precate
  (:require [leiningen.core :as lein]
            [clojure.java.io :as io])
  (:use [clojure.test]
        [leiningen.precate]))

(def p1 (lein/read-project (.getPath (io/resource "p1.clj"))))

(deftest test-p1
  (let [p1-full (suggest-project-map p1)
        p1 (tidy-project p1-full)]
    (is (= {:name "hi" :group "hello" :version "1.0.0"
            :dependencies [['ordered "1.1.0"]
                           ['org.clojure/clojure "1.3.0"]
                           ['something "0.0.1" :extension "pom" :classifier "sources"]]
            :min-lein-version "2.0.0"
            :profiles {:dev {:dependencies [['robert/hooke "1.1.3"]
                                            ['something-dev "0.0.1" :extension "pom"]]
                             :resource-paths ["junk"]}
                       :1.2.1 {:dependencies [['org.clojure/clojure "1.2.1"]
                                              ['something-early "0.0.1" :extension "pom"]]
                               }
                       :1.4.0 {:dependencies [['org.clojure/clojure "1.4.0-beta1"]
                                              ['slingshot "0.10.1"]
                                              ['cheshire "2.2.0"]
                                              ['something-pre "0.0.1" :extension "pom"]]}}
            :plugins [['lein-swank "1.4.1"]
                      ['lein-tar "1.0.6"]]}
           (dissoc p1 :source-paths :resource-paths :root)))
    (is (= ["src/clj"] (:source-paths p1)))
    (is (= ["stuff" "whatever"] (:resource-paths p1)))))
