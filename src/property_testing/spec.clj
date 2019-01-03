(ns property-testing.spec
  (:require
    [clojure.spec.alpha :as spec]
    [clojure.test.check :as tc]
    [clojure.test.check.clojure-test :refer [defspec]]
    [clojure.test.check.generators :as gen]
    [clojure.test.check.properties :as prop]))


; a simple spec for a 3d bounding box
(spec/def ::x1 int?)
(spec/def ::x2 int?)
(spec/def ::y1 int?)
(spec/def ::y2 int?)

(spec/def ::z1 int?)
(spec/def ::z2 int?)

; wrong
(spec/def ::bounding-box
  (spec/keys :req-un [::x1 ::x2 ::y1 ::y2]
             :opt-un [::z1 ::z2]))
; right
;(spec/def ::bounding-box
;  (spec/keys :req-un [::x1 ::x2 ::y1 ::y2 ::z1 ::z2]))


; we can create a generator directly from a spec
(def box-gen (spec/gen ::bounding-box))

#_(gen/sample box-gen)
#_(gen/generate box-gen 100)


; ------------------------------------------------------------------------------


; a simple volume function for our box
(defn volume [box]
  (let [{:keys [x1 x2 y1 y2 z1 z2]} box
        w (Math/abs (- x2 x1))
        h (Math/abs (- y2 y1))
        l (Math/abs (- z2 z1))]
    (* 1 w h l)   ; wrong
    ;(* 1N w h l) ; right
    ))

; hand-coded test: happy
#_(volume {:x1 0 :x2 10
           :y1 0 :y2 10
           :z1 0 :z2 2})

; test with random data: sometimes sad!
#_(volume (gen/generate box-gen))


; ------------------------------------------------------------------------------


; define a property which should hold true for all instances of our box
(def box-prop
  (prop/for-all
    [box box-gen]
    (let [{:keys [x1 x2 y1 y2 z1 z2]} box]
      (and (<= x1 x2)
           (<= y1 y2)
           (<= z1 z2)))))

; fails! let's fix it...
#_(tc/quick-check 1000 box-prop)


; a better generator that constrains the values a bit more
(def better-box-gen
  (gen/such-that
    (fn [{:keys [x1 x2 y1 y2 z1 z2]}]
      (and (<= x1 x2)
           (<= y1 y2)
           (<= z1 z2)))
    box-gen
    100))

#_(gen/sample better-box-gen)


; ensure that volume is always valid
(def pos-volume-prop
  (prop/for-all
    [box better-box-gen]
    (>= (volume box) 0)))

#_(tc/quick-check 1000 pos-volume-prop)


; defspec a unit test to reproduce failure! bam!

(defspec integer-overflow-test
  {:seed 1543603025227}
  pos-volume-prop)
