(ns property-testing.properties
  (:require
    [clojure.pprint :as pp]
    [clojure.test.check :as tc]
    [clojure.test.check.clojure-test :refer [defspec]]
    [clojure.test.check.generators :as gen]
    [clojure.test.check.properties :as prop]
    [clojure.test :refer :all]))


(defn run-length-encode [s]
  (->> (partition-by identity s)
       ;(mapcat (partial partition-all 9))
       (mapcat (juxt count first))
       (apply str)))

(defn run-length-decode [s]
  (->> (partition 2 s)
       (mapcat (fn [[i c]] (repeat (Character/getNumericValue i) c)))
       (apply str)))


(deftest rle-long-runs-test
  (is (= "9a2a1b2c" (run-length-encode "aaaaaaaaaaabcc"))))

(deftest rle-symmetry-test
  (let [s "aaaaaaaaaaabcc"]
    (is (= s (run-length-decode (run-length-encode s))))))


;; -----------------------------------------------------------------------------


(def rle-format-prop
  (prop/for-all
    [s gen/string]
    (let [rle (run-length-encode s)]
      (or (= "" s rle)
          (re-find #"(?s)^(\d.)+$" rle)))))

#_(tc/quick-check 1000 rle-format-prop)


(def rle-symmetry-prop
  (prop/for-all
    [s gen/string]
    (= s (run-length-decode (run-length-encode s)))))

#_(tc/quick-check 1000 rle-symmetry-prop)


(def rle-longruns-prop
  (prop/for-all
    [s (gen/let [c gen/char]
         (gen/fmap
           (partial apply str)
           (gen/tuple
             gen/string
             (gen/fmap (partial apply str) (gen/vector (gen/return c) 10 100))
             gen/string)))]
    (= s (run-length-decode (run-length-encode s)))))

#_(tc/quick-check 1000 rle-longruns-prop)


;; -----------------------------------------------------------------------------


(defspec rle-format-prop-test
  1000
  rle-format-prop)

(defspec rle-symmetry-prop-test
  1000
  rle-symmetry-prop)

(defspec rle-longruns-prop-test
  1000
  rle-longruns-prop)
