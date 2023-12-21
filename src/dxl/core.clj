(ns dxl.core
  (:refer-clojure :exclude [booleans])
  (:require
   [clojure.string :as string]))


(def aria-data-css-custom-prop-special-case-re #"^(aria-|data-|--).*")


(defn camel-case
  "Returns camel case version of the string, e.g. \"http-equiv\" becomes \"httpEquiv\"."
  [s]
  (if (or (keyword? s)
          (string? s)
          (symbol? s))
    (let [name-str (name s)]
                                        ; this is hot path so we want to use low-level interop
      (cond
        ;; (some? (re-matches aria-data-css-custom-prop-special-case-re name-str)) name-str
        (= (subs name-str 0 1) "'") (subs name-str 1)
        :else (string/replace name-str #"-(\w)" #(string/upper-case (second %)))))
    s))


(def booleans
  #{:allow-fullscreen
    :async
    :auto-focus
    :auto-play
    :checked
    :controls
    :default
    :disabled
    :form-no-validate
    :hidden
    :indeterminate
    :inert
    :is-map
    :loop
    :multiple
    :muted
    :nomodule
    :no-validate
    :open
    :plays-inline
    :read-only
    :required
    :reversed
    :seamless
    :selected})


(def properties
  (into
   #{:accept-charset
     :auto-capitalize
     :class
     :class-name
     :for
     :http-equiv
     :spell-check
     :style
     :read-only
     :value}
   booleans))


(defn property
  [k]
  (case k
    :class (symbol "-className")
    :for (symbol "-htmlFor")
    (str "-" (camel-case k))))


(defn event-handler?
  [k]
  (string/starts-with? (name k) "on"))


(def event-handler-name
  {:on-click "click"})


(defn reactive?
  [form]
  (or (= '! form)
      (when (and (seqable? form)
                 (not= `element (first form))
                 (not= `$ (first form)))
        (some reactive? form))))


(comment
  (reactive? `(! foo))
  ;; => true

  (reactive? `(do (! foo)))
  ;; => true

  (reactive? `(if false
                (! foo)
                :bar))
  ;; => true

  (reactive? `(when (! foo) (element (! bar))))
  ;; => true

  (reactive? `(element (! foo)))
  ;; => nil
)


(defmacro element
  [el & params]
  (let [[props children] (if (map? (first params))
                           [(first params) (rest params)]
                           [nil params])
        constructor `(.createElement js/document ~el)]
    (if (seq params)
      (let [el (with-meta (gensym "el") {:tag 'js})
            set-prop (fn [k v]
                       (cond
                         ;; TODO handle booleans
                         (contains? properties k)
                         `(set! (. ~el ~(property k)) ~v)

                         ;; TODO handle delegation in CLJS
                         (event-handler? k)
                         `(.addEventListener ~el ~(event-handler-name k) ~v)

                         ;; TODO handle string conversion
                         :else `(.setAttribute ~el ~(name k) ~v)))]
        `(let [~el ~constructor]
           ~@(for [[k v] props]
               (if (reactive? v)
                 `(let [rt# *runtime*]
                    (-effect *runtime* (fn [] (binding [*runtime* rt#]
                                                ~(set-prop k v)))))
                 (set-prop k v)))
           ~@(for [child children]
               (if (reactive? child)
                 `(insert! ~el (fn [] ~child))
                 `(.appendChild ~el (node ~child))))
           ~el))
      constructor)))


(macroexpand `(element "div"))

(macroexpand `(element "div" {:class "foo"}))

(macroexpand `(element "input" {:class "foo" :type "text"}))

(macroexpand `(element "div" (element "span")))


(macroexpand `(element "div" {:class (! foo)}))


(macroexpand `(element "div"
                {:class (! foo)}
                (when (! bar)
                  (element "span" (element "text" "hi")))
                (element {:class (! baz)})))


(defmacro $
  [type & params]
  (let [[props children] (if (map? (first params))
                           [(first params) (rest params)]
                           [nil params])]
    (if (keyword? type)
      `(element ~(name type) ~params)
      `(component ~type (assoc ~props :children ~children)))))


(defmacro with-runtime
  [runtime & body]
  `(binding [*runtime* ~runtime]
     ~@body))
