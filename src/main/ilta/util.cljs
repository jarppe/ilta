(ns ilta.util)


(defn today [] (js/Date.))
(defn now [] (js/Date.now))


(defn date->stamp [^js date]
  (let [y (.getFullYear date)
        m (inc (.getMonth date))
        d (.getDate date)]
    (str y
         "-"
         (if (< m 10) "0" "") m
         "-"
         (if (< d 10) "0" "") d)))


(def ^:private week-day ["Sunnuntai"
                         "Maanantai"
                         "Tiistai"
                         "Keskiviikko"
                         "Torstai"
                         "Perjantai"
                         "Lauantai"])


(defn date->str [^js date]
  (let [y (.getFullYear date)
        m (inc (.getMonth date))
        d (.getDate date)
        n (.getDay date)]
    (str (nth week-day n) " " d "." m "." y)))


(comment
  (date->stamp (today))
  ;; => "2023-08-01"

  (date->str (today))
  ;; => "Tiistai 1.8.2023" 
  )