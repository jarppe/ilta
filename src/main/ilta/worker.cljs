(ns ilta.worker)

(def ^js cache (js/caches.open "v2"))


(defn add-resources-to-cache [resources]
  (.then cache (fn [^js cache] (.addAll cache (clj->js resources)))))


(defn put-in-cache [req resp]
  (.then cache (fn [^js cache] (.put cache req resp))))


(defn on-fetch [^js event]
  (.respondWith event (let [request (.-request event)
                            url     (.-url request)]
                        (-> (js/fetch request)
                            (.then (fn [^js response]
                                     (js/console.log "worker: on-fetch: loaded from server" url)
                                     (-> (put-in-cache request (.clone response))
                                         (.then (fn [_] response)))))
                            (.catch (fn [_]
                                      (-> (js/caches.match request)
                                          (.then (fn [^js response]
                                                   (js/console.log "worker: on-fetch: loaded from cache" url)
                                                   response))
                                          (.catch (fn [error]
                                                    (js/console.log "worker: on-fetch: failed" url error)
                                                    (throw error))))))))))


(defn on-activate [^js event]
  (js/console.info "worker: on-activate")
  (when-let [preload (-> js/self .-registration .-navigationPreload)]
    (.waitUntil event (.enable preload))))


(defn on-install [^js event]
  (js/console.info "worker: on-install")
  (.waitUntil event (add-resources-to-cache (concat ["./index.html"
                                                     "./styles.css"
                                                     "./shared.js"
                                                     "./app.js"
                                                     "./favicon.ico"]))))


(defn on [event handler]
  (.addEventListener js/self event handler))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn init []
  (js/console.info "worker: initializing...")
  (on "activate" on-activate)
  (on "install" on-install)
  (on "fetch" on-fetch)
  (js/console.info "worker: done"))
