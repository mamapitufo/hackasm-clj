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

(defn first-pass
  "Accepts a HACK assembly program and removes all comments and white space. Maps
  any labels it finds to the next instruction number and adds them to a symbol
  table.

  Returns a vector with the cleaned up instructions as a vector and the symbol
  table."
  [src]
  (loop [instructions src
         scrubbed []
         symbol-table {}]
    (if (empty? instructions)
      [scrubbed symbol-table]
      (let [current (scrub (first instructions))
            remaining (rest instructions)]
        (cond
          (s/starts-with? current "(")
          (let [[_ label] (re-matches #"^\((.*)\)$" current)
                index (count scrubbed)]
            (recur remaining
                   scrubbed
                   (assoc symbol-table (keyword label) index)))
          (not-empty current)
          (recur remaining
                 (conj scrubbed current)
                 symbol-table)
          :else
          (recur remaining
                 scrubbed
                 symbol-table))))))

(defn- to-binary [num-string]
  (pprint/cl-format nil "~16,'0b" (Integer/parseInt num-string)))

(defn- parse-a [instruction symbol-table]
  (let [value (subs instruction 1)
        symb (keyword value)]
    (cond
      (re-matches #"^\d.*" value)
      [(to-binary value) symbol-table]

      (contains? (:symbols symbol-table) symb)
      (parse-a (format "@%s" (get-in symbol-table [:symbols symb]))
               symbol-table)

      :else
      (let [address (:next-var symbol-table)
            updated-st (-> symbol-table
                           (assoc-in [:symbols symb] address)
                           (assoc :next-var (inc address)))]
        (parse-a (format "@%s" address) updated-st)))))

(defn- parse-c [instruction]
  (let [parts (re-matches #"^(?:([^=]+)=)?([^;]+)(?:;(.*))?$" instruction)
        comp (tables/comp-table (keyword (parts 2)))
        dest (tables/dest-table (keyword (parts 1)) "000")
        jump (tables/jump-table (keyword (parts 3)) "000")]

    (format "111%s%s%s" comp dest jump)))

(defn parse-instruction
  "Parses `instruction` into HACK machine language. Returns a vector with the
  translated instruction and the updated symbol table."
  [instruction symbol-table]
  (if (s/starts-with? instruction "@")
    (parse-a instruction symbol-table)
    [(parse-c instruction) symbol-table]))

(defn assemble
  "Translates the list of ASM instructions into HACK machine language. Accepts
  a pre-populated symbol table with the default symbols."
  [instructions symbol-table]
  (loop [src instructions
         machine []
         st symbol-table]
    (if (empty? src)
      machine
      (let [[translated updated-st] (parse-instruction (first src) st)]
        (recur (rest src)
               (conj machine translated)
               updated-st)))))

(defn -main
  "Assembles the HACK assembly program 'src-file'."
  [src-file]
  (let [[instructions symbols] (first-pass (load-src src-file))
        symbol-table {:symbols symbols :next-var 16}
        machine-instructions (assemble instructions symbol-table)]

    (write-results src-file machine-instructions)))
