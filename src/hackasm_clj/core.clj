(ns hackasm-clj.core
  (:require [clojure.string :as s])
  (:gen-class))

(defn- load-src [src-file]
  (line-seq (clojure.java.io/reader src-file)))

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
  )
