(ns ilta.util)


(defn today [] (js/Date.))
(defn now [] (js/Date.now))


(defn date->id [^js date]
  (let [y (.getFullYear date)
        m (inc (.getMonth date))
        d (.getDate date)]
    (str (+ (* y 10000)
            (* m 100)
            d))))


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


(def ms-in-day (* 1000 60 60 24))


(defn get-week []
  (let [ts (now)]
    (->> (range 7)
         (map-indexed  (fn [i d]
                         (let [date (js/Date. (- ts (* ms-in-day d)))]
                           {:index i
                            :date  (date->id date)
                            :label (date->str date)})))
         (vec))))

(comment
  (date->id (today))
  ;; => "20230803"

  (date->str (today))
  ;; => "Torstai 3.8.2023"

  (get-week)
  ;; => [{:index 0, :date "20230803", :label "Torstai 3.8.2023"}
  ;;     {:index 1, :date "20230802", :label "Keskiviikko 2.8.2023"}
  ;;     {:index 2, :date "20230801", :label "Tiistai 1.8.2023"}
  ;;     {:index 3, :date "20230731", :label "Maanantai 31.7.2023"}
  ;;     {:index 4, :date "20230730", :label "Sunnuntai 30.7.2023"}
  ;;     {:index 5, :date "20230729", :label "Lauantai 29.7.2023"}
  ;;     {:index 6, :date "20230728", :label "Perjantai 28.7.2023"}] 
  )