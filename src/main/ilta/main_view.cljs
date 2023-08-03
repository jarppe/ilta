(ns ilta.main-view
  (:require [helix.core :as hx :refer [$ defnc <>]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [promesa.core :as p]
            [goog.functions :refer [debounce]]
            [ilta.util :as u]
            [ilta.storage :as s]))


(defnc EntryView [{:keys [entry]}]
  (let [[text set-text] (hooks/use-state (:text entry))
        save-text       (hooks/use-callback
                         [(:entryid entry)]
                         (debounce (fn [text]
                                     (s/save (assoc entry :text text)))
                                   400))]
    (d/li
     (d/textarea {:value       (or text "")
                  :placeholder "Hyvä asia tässä päivässä..."
                  :on-change   (fn [e]
                                 (let [text (-> e .-target .-value)]
                                   (set-text text)
                                   (save-text text)))}))))


(defnc DateView [{:keys [selected-date]}]
  (let [[entries set-entries] (hooks/use-state nil)]
    (hooks/use-effect
     [selected-date]
     (set-entries nil)
     (-> (s/entries-by-date selected-date)
         (p/then (fn [entries]
                   (-> (sort-by :created < entries)
                       (concat (let [ts (u/now)]
                                 (map (fn [n]
                                        {:entryid (js/crypto.randomUUID)
                                         :date    selected-date
                                         :created (+ ts n)})
                                      (range (max 0 (- 3 (count entries)))))))
                       set-entries))))
     nil)
    (when entries
      (let [[a b c & more] entries]
        (d/ul
         ($ EntryView {:entry a})
         ($ EntryView {:entry b})
         ($ EntryView {:entry c})
         (for [entry more]
           ($ EntryView {:key   (:entryid entry)
                         :entry entry})))))))


(def week (u/get-week))


(defnc MainView [_]
  (let [[selected-date set-selected-date] (hooks/use-state (first week))]
    (<> (d/header (d/img {:src "icon/ilta.svg"})
                  (d/h1 "Hyvää tässä päivässä"))
        (d/select {:selected  (:index selected-date)
                   :on-change (fn [e]
                                (let [selected-index (-> e .-target .-value (js/parseInt 10))]
                                  (set-selected-date (nth week selected-index))))}
                  (for [{:keys [index label]} week]
                    (d/option {:key   index
                               :value index}
                              label)))
        ($ DateView {:selected-date (:date selected-date)})
        (d/footer "Kauniita unia"
                  (d/span "Z")
                  (d/span "Z")
                  (d/span "Z")))))
