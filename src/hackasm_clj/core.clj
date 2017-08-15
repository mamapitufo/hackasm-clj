(ns hackasm-clj.core
  (:gen-class))

(defn- load-src [src-file]
  (line-seq (clojure.java.io/reader src-file)))

(defn -main
  "Assembles the HACK assembly program 'src-file'."
  [src-file]
  )
