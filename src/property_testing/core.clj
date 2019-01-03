(ns property-testing.core)


; test.check is a Clojure property testing library in the spirit of Haskell's QuickCheck

; - allows you declare properties of your functions to test with random data
;
; - works well as a complement, not a replacement, for traditional unit tests
;
; - clojure.spec's generators are built on top of test.check


; ------------------------------------------------------------------------------
; generators

; a declarative way to generate random data

; gen/sample - generates n simple samples, default 10
; gen/generate - generates a single example of variable complexity (see sizing)


; ------------------------------------------------------------------------------
; sizing

; test.check starts with small sizes so that it will catch easy bugs quickly
; without needing to generate very large input and then shrink it, and so that
; edge cases produced by small sizes have a good chance of being caught.

; because of the way sizing works, best practice is to run at least 200 trials
; in a test to expose it to the full range of data "sizes".


; ------------------------------------------------------------------------------
; shrinking

; failing tests are shrunk to the smallest possible failing case to make it
; easier to debug test failures. this is very cool.
