(defproject hackasm-clj "0.1.0-SNAPSHOT"
  :description "An assembler for the HACK machine described in the book Build a Modern Computer from First Principles"
  :url "http://www.nand2tetris.org/"
  :license {:name "The GNU General Public License v3"
            :url "https://www.gnu.org/licenses/#GPL"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot hackasm-clj.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
