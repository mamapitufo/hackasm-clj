(ns hackasm-clj.core-test
  (:require [clojure.test :refer :all]
            [hackasm-clj.core :refer :all]))

(deftest first-pass-scrubs-lines-test
  (testing "First pass will remove comments and trim white space"
    (is (= [] (first-pass ["// This is a comment"])))
    (is (= ["D=M+1"] (first-pass ["   D=M+1  // increment"])))
    (is (= ["@2" "D=A"] (first-pass ["// Example instructions"
                                     "   @2"
                                     "   D=A // d=2"])))))

(deftest parse-instruction-a-instructions-test
  (testing "A-instructions are correctly parsed (No symbols)"
    (is (= "0000000000000010" (parse-instruction "@2")))
    (is (= "0111111111111111" (parse-instruction "@32767")))
    (is (= "0000000000000000" (parse-instruction "@0")))))

(deftest parse-instruction-c-instructions-comp-test
  (testing "C-instructions `comp` fields are correctly parsed"
    (is (= "1110001100000000" (parse-instruction "D")))
    (is (= "1110111111000000" (parse-instruction "1")))
    (is (= "1110110111000000" (parse-instruction "A+1")))
    (is (= "1111000000000000" (parse-instruction "D&M")))))

(deftest parse-instruction-c-instructions-dest-test
  (testing "C-instructions `dest` fields are correctly parsed"
    (is (= "1110111111001000" (parse-instruction "M=1")))
    (is (= "1110111111110000" (parse-instruction "AD=1")))
    (is (= "1110111111111000" (parse-instruction "AMD=1")))))

(deftest parse-instruction-c-instructions-jump-test
  (testing "C-instructions `jump` fields are correctly parsed"
    (is (= "1110101010000111" (parse-instruction "0;JMP")))
    (is (= "1111110111010010" (parse-instruction "D=M+1;JEQ")))))
