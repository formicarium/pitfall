(ns pitfall.components
  (:require [com.stuartsierra.component :as component]
            [pitfall.routes :as routes]
            [pitfall.components.kubernetes :as components.kubernetes]
            [clj-service.components.pedestal :as components.pedestal]
            [clj-service.components.config :as components.config]
            [clj-service.components.webapp :as components.webapp]))

(defn base [env]
  {:config   (components.config/new-config (str (name env) ".edn"))
   :webapp   (component/using (components.webapp/new-webapp) [:config :kubernetes])
   :pedestal (component/using (components.pedestal/new-pedestal #'routes/routes {:io.pedestal.http/router :linear-search}) [:config :webapp])
   :kubernetes (component/using (components.kubernetes/new-kubernetes) [:config])})

(defn test-system [env]
  (merge
    (base env)
    {:kubernetes {}}))

(def systems
  {:prod base
   :test test-system
   :dev base})

(defn system-map
  [env]
  (prn env)
  (if-let [system-builder (get systems env)]
    (system-builder env)
    (throw (ex-info "Couldn't find system builder for environment" {:env env}))))
