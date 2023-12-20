(ns dxl.core
  (:refer-clojure :exclude [run!])
  (:require-macros [dxl.core]))


(defn node
  [x]
  (if (string? x)
    (js/document.createTextNode x)
    x))


(defprotocol Reference
  (-current [ref]))


(defprotocol Runtime
  (-component [scope f props])
  (-current-context [scope])
  (-effect [scope f])
  (-memo [scope f])
  (-root [scope f]))


(def ^:dynamic *runtime* nil)


(defn set-default-runtime!
  [runtime]
  (set! *runtime* runtime))


(defn !
  [ref]
  (-current ref))


(defn component
  [c props]
  (let [rt *runtime*]
    (-component
     *runtime*
     #(binding [*runtime* rt]
        (c %))
     props)))
