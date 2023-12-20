(ns app.main
  (:require
   [dxl.core :as d :refer [!]]
   [dxl.reagent :as dr]
   [reagent.ratom :as r]))


(def state (r/atom true))

(comment
  (reset! state false)

  (reset! state true)
)


(defn app
  []
  (d/element "div"
    ;; {:class (if (! state) "foo" "bar")}
    (if (! state)
      "foo"
      "bar")))


(comment
  (macroexpand
   '(d/element "div"
      (if (! state)
        "foo"
        "bar"))))


(defn ^:dev/after-load start!
  []
  (dr/start! (js/document.getElementById "root") app))
