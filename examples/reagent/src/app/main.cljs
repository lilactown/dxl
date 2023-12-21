(ns app.main
  (:require
   [dxl.core :as d :refer [!]]
   [dxl.reagent :as dr]
   [reagent.ratom :as r]))


(def state (r/atom 0))

(comment
  (swap! state inc)

  (swap! state dec)
)


(defn app
  []
  (prn :app)
  (d/element "div"
    (! state)
    (d/element "button"
      {:on-click #(swap! state inc)}
      "inc")))


(comment
  (macroexpand
   '(d/element "div"
      (if (! state)
        "foo"
        "bar"))))


(defn ^:dev/after-load start!
  []
  (dr/start! (js/document.getElementById "root") app))
