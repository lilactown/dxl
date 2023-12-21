(ns dxl.core
  (:refer-clojure :exclude [run!])
  (:require-macros [dxl.core]))


(defn node
  [x]
  (if (or (string? x) (number? x))
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


(defn insert!
  ([parent f]
   (let [*prev (atom nil)
         rt *runtime*]
     (-effect
      *runtime*
      (fn []
        (binding [*runtime* rt]
          (if-let [prev @*prev]
            (doto (node (f))
              (as-> node (.replaceChild parent node prev)) ; el.rC(new, old)
              (->> (reset! *prev)))
            (doto (node (f))
              (->> (.appendChild parent))
              (->> (reset! *prev)))))))))
  ([parent f sibling]))


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
