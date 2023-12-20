# dxl

dxl is a ClojureScript library for constructing "**D**OM E**x**pressions." It is
heavily inspired by [ryansolid/dom-expressions](https://github.com/ryansolid/dom-expressions/tree/main)
used by [SolidJS](https://github.com/solidjs/solid).


## Example

```clojure
(ns user
  (:require
    [dx.core :as d :refer [!]]))

(d/div) ;; compiles to (.createElement js/document "div")

(d/input {:type "text"})
;; compiles to
;; (let [^js el0 (.createElement js/document "input")]
;;   (set! (.-type el0) "text")
;;   el0)


(let [class (atom "foo")]
  (d/div {:class (! class)}))
;; compiles to
;; (let [class (atom "foo")]
;;   (let [^js el1 (.createElement js/document "div")]
;;     (dx.core/run!
;;       (fn []
;;         (set! (.-className el1) (! class))))
;;     el1))
```
