(ns ilta.app
  (:require ["react-dom/client" :refer [createRoot]]
            [promesa.core :as p]
            [helix.core :as hx :refer [$]]
            [ilta.main-view :refer [MainView]]))


(defonce root (createRoot (js/document.getElementById "app")))


(defn ^:export start []
  (js/console.log "start: registering worker...")
  (-> (js/navigator.serviceWorker.register "worker.js" #js {:scope "./"})
      (p/then (fn [_] (js/console.log "start: worker registered")))
      (p/catch (fn [error] (js/console.log "start: worker failed" error))))
  (js/console.log "start: rendering app")
  (.render root ($ MainView))
  (js/console.log "start: ready"))
