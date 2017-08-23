(ns hackasm-clj.core-test
  (:require [clojure.test :refer :all]
            [hackasm-clj.core :refer :all]
            [hackasm-clj.example-code :as examples]))

(deftest first-pass-scrubs-lines-test
  (testing "First pass will remove comments and trim white space"
    (is (= [[] {}] (first-pass ["// This is a comment"])))
    (is (= [["D=M+1"] {}] (first-pass ["   D=M+1  // increment"])))
    (is (= [["@2" "D=A"] {}] (first-pass ["// Example instructions"
                                          "   @2"
                                          "   D=A // d=2"])))))

(deftest first-pass-adds-labels-test
  (testing "First pass will remove labels and add them to the symbol table"
    (is (= [["@END" "0;JMP"]
            {:END 0}]
           (first-pass ["(END)"
                        "   @END"
                        "   0;JMP"])))
    (is (= [examples/max-first-pass
            {:OUTPUT_FIRST 10
             :OUTPUT_D 12
             :INFINITE_LOOP 14}]
           (first-pass examples/max-asm)))))

(deftest parse-instruction-a-instructions-test
  (testing "A-instructions are correctly parsed (No symbols)"
    (is (= "0000000000000010" (first (parse-instruction "@2" {}))))
    (is (= "0111111111111111" (first (parse-instruction "@32767" {}))))
    (is (= "0000000000000000" (first (parse-instruction "@0" {}))))))

(deftest parse-instruction-a-instructions-existing-symbol-test
  (testing "A-instructions are correctly parsed when a symbol exists."
    (is (= "0000000000010000"
           (first (parse-instruction "@test" {:symbols {:test 16}}))))
    (is (= "0000000000000101"
           (first (parse-instruction "@END" {:symbols {:END 5 :test 16}}))))))

(deftest parse-instruction-a-instruction-new-symbol-test
  (testing "A-instructions are correctly parsed and symbol table is updated"
    (is (= ["0000000000010000" {:symbols {:test 16}
                                :next-var 17}]
           (parse-instruction "@test" {:symbols {} :next-var 16})))
    (is (= ["0000000000010001" {:symbols {:test 16 :another 17}
                                :next-var 18}]
           (parse-instruction "@another" {:symbols {:test 16} :next-var 17})))))

(deftest parse-instruction-c-instructions-comp-test
  (testing "C-instructions `comp` fields are correctly parsed"
    (is (= "1110001100000000" (first (parse-instruction "D" {}))))
    (is (= "1110111111000000" (first (parse-instruction "1" {}))))
    (is (= "1110110111000000" (first (parse-instruction "A+1" {}))))
    (is (= "1111000000000000" (first (parse-instruction "D&M" {}))))))

(deftest parse-instruction-c-instructions-dest-test
  (testing "C-instructions `dest` fields are correctly parsed"
    (is (= "1110111111001000" (first (parse-instruction "M=1" {}))))
    (is (= "1110111111110000" (first (parse-instruction "AD=1" {}))))
    (is (= "1110111111111000" (first (parse-instruction "AMD=1" {}))))))

(deftest parse-instruction-c-instructions-jump-test
  (testing "C-instructions `jump` fields are correctly parsed"
    (is (= "1110101010000111" (first (parse-instruction "0;JMP" {}))))
    (is (= "1111110111010010" (first (parse-instruction "D=M+1;JEQ" {}))))))

(deftest assemble-test
  (testing "Correctly assembles a whole program"
    (is (= examples/add-hack (assemble examples/add-asm {})))
    (is (= examples/rect-hack (assemble examples/rect-asm {})))))
