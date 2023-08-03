(ns ilta.storage
  (:require [clojure.string :as str]
            [promesa.core :as p]
            [ilta.util :as u]))


(def readonly "readonly")
(def readwrite "readwrite")


(def db-name "ilta_db")
(def db-version 1)
(def entry-store "entries")
(def entry-spec #js {:keyPath "entryid"})
(def date-index "date_index")


(defn- make-error [^js e & message]
  (let [ex (js/Error.)]
    (set! (.-message ex) (str (str/join " " message) ": " (-> e .-target .-errorCode)))
    ex))


(defonce db (atom nil))


(defn- create-object-store [^js db]
  (let [^js store (.createObjectStore db entry-store entry-spec)]
    (.createIndex store date-index "date" #js {:unique false})
    store))


(defn- init! []
  (-> (p/create (fn [resolve reject]
                  (let [^js r (js/window.indexedDB.open db-name db-version)]
                    (set! (.-onupgradeneeded r) (fn [^js e] (create-object-store (-> e .-target .-result))))
                    (set! (.-onsuccess r) (fn [^js e] (resolve (-> e .-target .-result))))
                    (set! (.-onerror r) (fn [^js e] (reject (make-error e "can't open")))))))
      (p/then (fn [opened-db]
                (reset! db opened-db)
                true))
      (p/catch (fn [err]
                 (js/console.error err)
                 (throw err)))))


(defn load [entryid]
  (p/create (fn [resolve reject]
              (let [req (-> @db
                            (.transaction entry-store readonly)
                            (.objectStore entry-store)
                            (.get entryid))]
                (set! (.-onsuccess req) (fn [^js e] (resolve (-> e .-target .-result (js->clj {:keywordize-keys true})))))
                (set! (.-onerror req) (fn [^js e] (reject (make-error e "can't load" entryid))))))))


(defn save [entry]
  (p/create (fn [resolve reject]
              (let [req (-> (.transaction @db entry-store readwrite)
                            (.objectStore entry-store)
                            (.put (clj->js entry)))]
                (set! (.-onsuccess req) (fn [^js e] (resolve (-> e .-target .-result))))
                (set! (.-onerror req) (fn [^js e] (reject (make-error e "can't save"))))))))


(defn entries-by-date [date]
  (p/create (fn [resolve reject]
              (let [req (-> (.transaction @db entry-store readonly)
                            (.objectStore entry-store)
                            (.index date-index)
                            (.getAll date))]
                (set! (.-onsuccess req) (fn [^js e] (resolve (mapv #(js->clj % {:keywordize-keys true})
                                                                   (-> e .-target .-result)))))
                (set! (.-onerror req) (fn [^js e] (reject (make-error e "error"))))))))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defonce __do-init__ (init!))


(comment

  (-> (js/window.indexedDB.deleteDatabase db-name)
      (p/then (fn [_] (println "Deleted")))
      (p/catch (fn [e] (println "Error" e))))

  (-> (save {:date    (-> (u/today) (u/date->id))
             :text    "FOOOOO"
             :entryid nil})
      (p/then (fn [resp] (println "save: ok" resp)))
      (p/catch (fn [resp] (println "save: error" resp))))

  (-> (load "1")
      (p/then (fn [resp] (println "ok" resp)))
      (p/catch (fn [resp] (println "error" resp))))

  (-> (entries-by-date (-> (u/today) (u/date->id)))
      (p/then (fn [resp] (println resp)))
      (p/catch (fn [resp] (println "error" resp))))

  (save {:date    "20230803"
         :text    "fofo fofo baba"
         :entryid 47})
  ;
  )
