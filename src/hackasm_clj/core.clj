(ns hackasm-clj.core
  (:require [clojure.string :as s])
  (:gen-class))

(defn- load-src [src-file]
  (line-seq (clojure.java.io/reader src-file)))

(defn- write-results [src-path machine-instructions]
  (let [results-path (s/replace src-path #"\.asm$" ".hack")]
    (spit results-path (s/join "\n" machine-instructions))))

(defn- scrub [line]
  (-> line
      (s/replace #"//.*$" "")
      s/trim))

(defn- first-pass [src]
  (->> src
       (map scrub)
       (filter not-empty)))

(defn -main
  "Assembles the HACK assembly program 'src-file'."
  [src-file]
  (let [instructions (first-pass (load-src src-file))
        machine-instructions instructions]

    (write-results src-file machine-instructions)))
