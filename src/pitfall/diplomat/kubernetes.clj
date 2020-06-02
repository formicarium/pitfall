(ns pitfall.diplomat.kubernetes
  (:require [kubernetes-api.core :as k8s]
            [cheshire.core :as json]
            [clj-service.protocols.config :as config]
            [pitfall.components.kubernetes :as components.kubernetes]))

(defn get-mocks [devspace {:keys [kubernetes config]}]
  (->> {:kind :ConfigMap
        :action :get
        :request {:name      (or (config/get-maybe config :mocks-configmap-name) "fmc-mocks")
                  :namespace devspace}}
       (k8s/invoke (components.kubernetes/client kubernetes))
       :data
       (map (fn [[k v]] [k (json/parse-string v true)]))
       (into {})))
