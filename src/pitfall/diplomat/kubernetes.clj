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
    (log/info :config-map config-map)
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
    (log/info :config-map config-map)
    config-map))

(defn tap [x]
  (log/info :tap x)
  x)

(defn build-config-map
  [old-config-map new-data]
  {:api-version (:apiVersion old-config-map)
   :kind        (:kind old-config-map)
   :metadata (select-keys (:metadata old-config-map) [:labels :annotations])
   :data (merge (:data old-config-map) new-data)})

(defn patch-mocks
  [devspace data components]
  (-> (get-mocks-config-map devspace components)
      (build-config-map data)
      adapters.mocks/externalize-mocks-config-map
      tap
      (update-mocks-config-map devspace components)))
