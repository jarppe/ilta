(ns ilta.app
  (:require ["react-dom/client" :refer [createRoot]]
            [helix.core :as hx :refer [$ defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [promesa.core :as p]
            [ilta.util :as u]
            [ilta.storage :as s]))



(goog-define DEV false)


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn stop []
  (js/console.log "Stopping..."))


(defonce root (-> (js/document.getElementById "app")
                  (createRoot)))


(defnc MainView [_]
  (d/h1 "Hello"))


(defn ^:export start []
  (js/console.log "Starting in" (if DEV "DEV" "PRODUCTION") "mode...")
  (.render root ($ MainView)))
