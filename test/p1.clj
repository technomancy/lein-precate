(defproject hello/hi "1.0.0"
  :dependencies [[ordered "1.1.0"]
                 [clojure "1.3.0"]
                 [something "0.0.1" :type "pom" :classifier "sources"]]
  :dev-dependencies [[swank-clojure "1.4.0"]
                     [lein-tar "1.0.6"]
                     [robert/hooke "1.1.3"]
                     [lein-multi "1.0.0"]
                     [something-dev "0.0.1" :type "pom"]]
  :multi-deps {"1.2.1" [[org.clojure/clojure "1.2.1"]
                        [something-early "0.0.1" :type "pom"]]
               "1.4.0" [[org.clojure/clojure "1.4.0-beta1"]
                        [slingshot "0.10.1"]
                        [cheshire "2.2.0"]
                        [something-pre "0.0.1" :type "pom"]]}
  :resource-paths ["stuff"]
  :resources-path "whatever"
  :source-path "src/clj"
  :extra-classpath-dirs ["junk"])