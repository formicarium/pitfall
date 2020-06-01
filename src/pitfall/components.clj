(ns pitfall.components
  (:require [com.stuartsierra.component :as component]
            [pitfall.routes :as routes]
            [clj-service.components.pedestal :as components.pedestal]
            [clj-service.components.config :as components.config]
            [clj-service.components.webapp :as components.webapp]))

(defn base [env]
  {:config   (components.config/new-config (str (name env) ".edn"))
   :webapp   (component/using (components.webapp/new-webapp) [:config])
   :pedestal (component/using (components.pedestal/new-pedestal #'routes/routes) [:config :webapp])})

(defn test-system [env]
  (merge
   (base env)
   {:k8s-api-server {}}))

(defn system-map
  [env]
  (case env
    :prod (test-system :base)
    :test (test-system :test)
    :dev (test-system :dev)
    (base env)))
