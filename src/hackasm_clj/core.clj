(ns hackasm-clj.core
  (:require [clojure.string :as s]
            [clojure.pprint :as pprint]
            [hackasm-clj.tables :as tables])
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

(defn- to-binary [num-string]
  (pprint/cl-format nil "~16,'0b" (Integer/parseInt num-string)))

(defn- parse-a [instruction]
  (to-binary (subs instruction 1)))

(defn- parse-c [instruction]
  (let [parts (re-matches #"^(?:([^=]+)=)?([^;]+)(?:;(.*))?$" instruction)
        comp (tables/comp-table (keyword (parts 2)))
        dest (tables/dest-table (keyword (parts 1)) "000")
        jump (tables/jump-table (keyword (parts 3)) "000")]

    (format "111%s%s%s" comp dest jump)))

(defn- parse-instruction [instruction]
  (if (s/starts-with? instruction "@")
    (parse-a instruction)
    (parse-c instruction)))

(defn -main
  "Assembles the HACK assembly program 'src-file'."
  [src-file]
  (let [instructions (first-pass (load-src src-file))
        machine-instructions (map parse-instruction instructions)]

    (write-results src-file machine-instructions)))
