(ns dxl.reagent
  (:require
   [dxl.core :as d]
   [reagent.ratom :as r]))


(extend-protocol d/Reference
  r/RAtom
  (-current [r] (deref r))

  r/Track
  (-current [r] (deref r))

  r/RCursor
  (-current [r] (deref r))

  r/Reaction
  (-current [r] (deref r))

  r/Wrapper
  (-current [r] (deref r)))


(deftype ReagentRuntime []
  d/Runtime
  (-component [_ f props]
    (r/run-in-reaction #(f props) #js {} "root" identity {}))
  (-current-context [_] r/*ratom-context*)
  (-effect [_ f] (r/make-track! f nil))
  (-memo [_ f] (r/make-reaction f))
  (-root [scope f]
    (r/run-in-reaction f scope "root" identity {})))


(defn start!
  [root-el root-component]
  (d/with-runtime (->ReagentRuntime)
    (.replaceChildren root-el (d/-root d/*runtime* root-component))))
