# dxl

dxl is the "**D**OM E**x**pression **L**anguage", a library for building web UI
frameworks heavily inspired by [ryansolid/dom-expressions](https://github.com/ryansolid/dom-expressions/tree/main)
used by [SolidJS](https://github.com/solidjs/solid).


## Example

```clojure
(ns user
  (:require
    [dxl.core :as d :refer [! $]]))

($ :div) ;; compiles to (.createElement js/document "div")

($ :input {:type "text"})
;; compiles to
;; (let [^js el0 (.createElement js/document "input")]
;;   (set! (.-type el0) "text")
;;   el0)


(let [class (atom "foo")]
  ($ :div {:class (! class)}))
;; compiles to
;; (let [class (atom "foo")]
;;   (let [^js el1 (.createElement js/document "div")]
;;     (dx.core/-effect *runtime*
;;       (fn []
;;         (set! (.-className el1) (-current *runtime* class))))
;;     el1))
```
