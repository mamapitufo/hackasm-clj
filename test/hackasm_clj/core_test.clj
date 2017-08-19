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
