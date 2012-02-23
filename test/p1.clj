(defproject hi "1.0.0"
  :dependencies [[ordered "1.1.0"]
                 [clojure "1.3.0"]]
  :dev-dependencies [[swank-clojure "1.4.0"]
                     [lein-tar "1.0.6"]
                     [robert/hooke "1.1.3"]
                     [lein-multi "1.0.0"]]
  :multi-deps {"1.2.1" [[org.clojure/clojure "1.2.1"]]
               "1.4.0" [[org.clojure/clojure "1.4.0-beta1"]
                        [slingshot "0.10.1"]
                        [cheshire "2.2.0"]]}
  :resources-path "whatever"
  :source-path "src/clj"
  :extra-classpath-dirs ["junk"])