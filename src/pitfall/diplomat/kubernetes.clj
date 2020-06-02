(ns pitfall.diplomat.kubernetes
  (:require [kubernetes-api.core :as k8s]
            [io.pedestal.log :as log]
            [cheshire.core :as json]
            [clj-service.protocols.config :as config]
            [pitfall.adapters.mocks :as adapters.mocks]
            [pitfall.components.kubernetes :as components.kubernetes]))

(defn get-mocks-config-map [devspace {:keys [kubernetes config]}]
  (let [config-map-name (or (config/get-maybe config :mocks-config-map-name) "fmc-mocks")
        _               (log/info :log :get-config-map :devspace devspace :config-map-name config-map-name)
        config-map      (k8s/invoke (components.kubernetes/client kubernetes)
                                    {:kind :ConfigMap
                                     :action :get
                                     :request {:name      config-map-name
                                               :namespace devspace}})]
    (log/debug :config-map config-map)
    config-map))

(defn get-mocks [devspace components]
  (->> (get-mocks-config-map devspace components)
       adapters.mocks/internalize-mocks-config-map
       :data))

(defn update-mocks-config-map [config-map devspace {:keys [kubernetes config]}]
  (let [config-map-name (or (config/get-maybe config :mocks-config-map-name) "fmc-mocks")
        _               (log/info :log :update-config-map :devspace devspace :config-map-name config-map-name)
        config-map      (k8s/invoke (components.kubernetes/client kubernetes)
                                    {:kind :ConfigMap
                                     :action :update
                                     :request {:name      config-map-name
                                               :namespace devspace
                                               :request   config-map}})]
    (log/debug :config-map config-map)
    config-map))

(defn patch-mocks
  [devspace data components]
  (-> (get-mocks-config-map devspace components)
      (update :data #(merge % data))
      adapters.mocks/externalize-mocks-config-map
      (update-mocks-config-map devspace components)))
