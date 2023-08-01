(ns ilta.storage
  (:refer-clojure :exclude [get set list])
  (:require [promesa.core :as p]))


(defn init []
  (p/resolved true))


(defn get [stamp]
  (p/resolved {:id      "x"
               :date    "y"
               :ts      1
               :entries ["foo" "bar" "boz"]}))


(defn set [stamp data]
  (p/resolved true))


(defn list [n]
  (p/resolved [{:id      "x"
                :date    "y"
                :ts      1
                :entries ["foo" "bar" "boz"]}]))
