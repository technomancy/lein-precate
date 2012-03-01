(ns leiningen.precate
  (:require [clojure.pprint :as pprint]
            [clojure.java.io :as io]))

(defn defaults [project]
  (let [default-file (io/file (:root project) "default-project.clj")]
    (try
      (spit default-file "(defproject dummy \"1.0\")")
      (require 'leiningen.core) ; dynamic so it at least loads in lein2
      ((resolve 'leiningen.core/read-project) (str default-file))
      (finally (.delete default-file)))))

(defn- tidy-key [default-project [k v]]
  (or (= (default-project k) v)
      ;; = on regexes is broken. ಠ_ಠ
      (and (#{:uberjar-exclusions :jar-exclusions} k)
           (= (str (default-project k) (str v))))))

(defn tidy-project [project]
  (let [default-project (defaults project)]
    (into {:root (str (:root project) "/")}
          (remove (partial tidy-key default-project) project))))

(defn suggest-profiles []
  ;; TODO:
  ;; ~/.lein/plugins
  ;; leiningen-auth
  ;; user-settings
  )

(defn- plugin? [[artifact _]]
  (re-find #"^lein-" (name artifact)))

(defn dev-dependencies [project]
  (if-let [dd (:dev-dependencies project)]
    (let [[plugins dev] ((juxt filter remove) plugin? dd)]
      (-> (dissoc project :dev-dependencies)
          (update-in [:plugins] (fnil into {}) plugins)
          (update-in [:profiles :dev] (fnil into {}) {})
          (update-in [:profiles :dev :dependencies] (fnil into {}) dev)))
    project))

(defn multi-deps-profile [project [profile deps]]
  (update-in project [:profiles (keyword profile) :dependencies]
             (fnil into {}) deps))

(defn multi-deps [project]
  (let [deps (:multi-deps project)
        project (dissoc project :multi-deps)]
    (reduce multi-deps-profile project deps)))

(def dev-deps-special-cases {'swank-clojure ['lein-swank "1.4.1"]
                             'lein-multi nil})

(defn special-case-dep [project [dev-dep replacement]]
  (let [find #(= dev-dep (first %))
        contains-old? (some find (:dev-dependencies project))
        project (update-in project [:dev-dependencies]
                           (fn [dev-deps] (remove find dev-deps)))]
    (if (and contains-old? replacement)
      (update-in project [:plugins] (fnil conj {}) replacement)
      project)))

(defn add-org-clojure [dependencies]
  (for [[artifact version] dependencies]
    (if (#{'clojure 'clojure-contrib} artifact)
      [(symbol "org.clojure" (name artifact)) version]
      [artifact version])))

(defn special-case-dev-deps [project]
  (reduce special-case-dep
          (update-in project [:dependencies] add-org-clojure)
          dev-deps-special-cases))

(defn min-lein-version [project]
  (assoc project :min-lein-version "2.0.0"))

(defn extra-classpath-dirs [project]
  (if-let [ecd (:extra-classpath-dirs project)]
    (-> (dissoc project :extra-classpath-dirs)
        (update-in [:profiles :dev] (fnil into {}) {})
        (update-in [:profiles :dev :resource-paths] (fnil into []) ecd))
    project))

(defn repositories-format [project]
  (update-in project [:repositories] (partial into {})))

(defn dependencies-format [project]
  (update-in project [:dependencies] (partial into {})))

(def vec-paths {:source-path :source-paths, :java-source-path :java-source-paths
                :test-path :test-paths, :resources-path :resource-paths})

(defn- vec-pathize [project key]
  (if-let [path (project key)]
    (assoc (dissoc project key) (vec-paths key)
           (vec (for [p (conj (project (vec-paths key)) path)]
                  (.replace p (:root project) ""))))
    project))

(defn paths-as-vectors [project]
  (reduce vec-pathize project (keys vec-paths)))

(defn dissoc-empty [project key]
  (if (empty? (project key))
    (dissoc project key)
    project))

(defn dissoc-empty-keys [project]
  (-> project
      (dissoc-empty :plugins)
      (dissoc-empty :repositories)
      (dissoc-empty :dependencies)))

(defn suggest-project-map [project]
  (-> project
      tidy-project
      special-case-dev-deps
      dev-dependencies
      multi-deps
      min-lein-version
      extra-classpath-dirs
      repositories-format
      dependencies-format
      dissoc-empty-keys
      paths-as-vectors))

(defn project-map-to-defproject [project]
  `(~'defproject ~(symbol (or (:group project)
                              (:name project))) ~(:version project)
     ~@(apply concat (dissoc project :name :group :version :root))))

(defn pprint-project [project]
  (pprint/with-pprint-dispatch pprint/code-dispatch
    (pprint/cl-format true "~<(~;~a ~a ~s~^ ~1i~_~@<~@{~<~w~^ ~w~:>~^ ~_~}~:>~;)~:>~%"
                      (concat (take 3 project)
                              (list (partition 2 (drop 3 project)))))))

(defn precate
  "Suggest a new project.clj that's compatible with Leiningen 2."
  [project]
  (when (.startsWith (System/getenv "LEIN_VERSION") "2")
    (println "This plugin is designed to be used with Leiningen 1.x."))
  (when (:eval-in-leiningen project)
    (println "This plugin is not intended for use on other plugins.")
    (println "Please see https://github.com/technomancy/leiningen/blob/master/doc/PLUGINS.md"))
  (suggest-profiles)
  (pprint-project (project-map-to-defproject (suggest-project-map project)))
  (flush))
