(ns pitfall.diplomat.kubernetes
  (:require [kubernetes-api.core :as k8s]
            [io.pedestal.log :as log]
            [cheshire.core :as json]
            [clj-service.protocols.config :as config]
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
       :data
       (map (fn [[k v]] [k (json/parse-string v true)]))
       (into {})))

(defn patch-mocks
  [devspace data {:keys [kubernetes] :as components}]
  (->> (update (get-mocks-config-map devspace components) :data #(merge % data))
       (k8s/invoke (components.kubernetes/client kubernetes))))
