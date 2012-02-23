# lein-precate

Suggest a new project.clj that's compatible with Leiningen 2.

## Usage

```
$ lein plugin install lein-precate 0.1.0
$ cat project.clj # the original 1.x-compatible version:
(defproject clojure-http-client "1.1.1-SNAPSHOT"
  :description "An HTTP client for Clojure."
  :source-path "src/clj"
  :extra-classpath-dirs ["dumb-stuff"]
  :dev-dependencies [[swank-clojure "1.3.4"]]
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [org.clojure/clojure-contrib "1.2.0"]])

$ lein precate # let's see how that would look for Leiningen 2
(defproject clojure-http-client "1.1.1-SNAPSHOT"
  :description "An HTTP client for Clojure."
  :source-paths ["src/clj"]
  :dependencies {org.clojure/clojure "1.2.1", 
                 org.clojure/clojure-contrib "1.2.0"}
  :profiles {:dev
              {:resource-paths ["dumb-stuff"],
               :dependencies {swank-clojure "1.3.4"}}}
  :min-lein-version "2.0.0")
```

This plugin is designed to be run under Leiningen 1.x.

## License

Copyright © 2012 Phil Hagelberg

Distributed under the Eclipse Public License, the same as Clojure.
