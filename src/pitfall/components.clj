(ns pitfall.components
  (:require [com.stuartsierra.component :as component]
            [pitfall.routes :as routes]
            [clj-service.components.pedestal :as components.pedestal]
            [clj-service.components.config :as components.config]
            [clj-service.components.webapp :as components.webapp]))

(defn base [env]
  {:config   (components.config/new-config (str (name env) ".edn"))
   :webapp   (component/using (components.webapp/new-webapp) [:config :config-server :k8s-client])
   :pedestal (component/using (components.pedestal/new-pedestal #'routes/routes) [:config :webapp])})

(defn test-system [env]
  (merge
   (base env)
   {:k8s-api-server {}}))

(defn system-map
  [env]
  (case env
    :test (test-system env)
    (base env)))
