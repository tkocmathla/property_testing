(ns property-testing.generators
  (:require
    [clojure.pprint :as pp]
    [clojure.test.check.generators :as gen]))

; ------------------------------------------------------------------------------
; Constant generator

(gen/sample (gen/return "const"))


; ------------------------------------------------------------------------------
; Simple generators

(gen/sample gen/nat)
(gen/sample gen/string)
(gen/sample gen/keyword)
(gen/sample (gen/one-of [gen/nat gen/string gen/keyword]))
(gen/sample (gen/elements #{:apple :orange :banana}))


; ------------------------------------------------------------------------------
; Collection generators

(gen/sample (gen/vector gen/nat))
(gen/sample (gen/vector-distinct gen/nat))
(gen/sample (gen/tuple gen/keyword (gen/return nil) gen/double))
(gen/sample (gen/map gen/keyword gen/string))
(pp/pprint (gen/sample (gen/vector-distinct-by :name (gen/hash-map :name gen/string-alphanumeric, :age gen/nat))))


; ------------------------------------------------------------------------------
; Modifiers

(def fruit-basket-gen (gen/vector (gen/elements #{:apple :orange :banana})))
(gen/sample
  (gen/such-that
    (fn [v] (>= (count (filter #{:banana} v)) 2))
    fruit-basket-gen))

(gen/sample
  (gen/fmap (fn [v] (conj v 42)) (gen/set gen/int)))

(gen/sample
  (gen/fmap (partial apply sorted-set) (gen/set gen/int)))


; ------------------------------------------------------------------------------
; Combinators

(pp/pprint
  (gen/sample
    (gen/bind
      (gen/vector gen/nat)
      (fn [v]
        (gen/tuple
          (gen/return v)
          (gen/such-that #(< (count v) (count %)) (gen/vector gen/nat)))))))

; let ~= fmap + bind
(pp/pprint
  (gen/sample
    (gen/let
      [v (gen/vector gen/nat)]
      (gen/tuple
        (gen/return v)
        (gen/such-that #(< (count v) (count %)) (gen/vector gen/nat))))))
