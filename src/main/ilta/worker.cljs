(ns ilta.worker
  (:require [promesa.core :as p]))


(def resources #js ["./index.html"
                    "./styles.css"
                    "./shared.js"
                    "./app.js"])


(def ^js cache (js/caches.open "v1"))


(defn put-in-cache [req resp]
  (p/then cache (fn [^js cache] (.put cache req resp))))


(defn on-fetch [^js event]
  (.respondWith event (let [request (.-request event)
                            url     (.-url request)]
                        (-> (js/fetch request)
                            (p/then (fn [^js response]
                                      (js/console.log "worker: on-fetch: loaded from server" url)
                                      (-> (put-in-cache request (.clone response))
                                          (p/then (fn [_] response)))))
                            (p/catch (fn [_]
                                       (-> (js/caches.match request)
                                           (p/then (fn [^js response]
                                                     (js/console.log "worker: on-fetch: loaded from cache" url)
                                                     response))
                                           (p/catch (fn [error]
                                                      (js/console.log "worker: on-fetch: failed" url error)
                                                      (throw error))))))))))


(defn on-activate [^js event]
  (when-let [preload (-> js/self .-registration .-navigationPreload)]
    (.waitUntil event (.enable preload))))


(defn on-install [^js event]
  (.waitUntil event (p/then cache (fn [^js cache] (.addAll cache resources)))))


(defn on [event handler]
  (.addEventListener js/self event handler))


(defn init []
  (js/console.info "worker: initializing...")
  (on "activate" on-activate)
  (on "install" on-install)
  (on "fetch" on-fetch)
  (js/console.info "worker: done"))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defonce __init__ (init))
