(ns app.main
  (:require
   [dxl.core :as d :refer [! $]]
   [dxl.reagent :as dr]
   [reagent.ratom :as r]))


(defn counter
  []
  (let [*state (r/atom 0)]
    ($ :div
      ($ :button
        {:on-click #(swap! *state dec)}
        "dec")
      (! *state)
      ($ :button
        {:on-click #(swap! *state inc)}
        "inc"))))


(defn app
  []
  ($ :div
    ($ counter)
    ($ counter)))


(defn ^:dev/after-load start!
  []
  (dr/start! (js/document.getElementById "root") app))
