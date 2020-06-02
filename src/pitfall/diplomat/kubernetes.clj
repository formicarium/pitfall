(ns pitfall.diplomat.kubernetes
  (:require [kubernetes-api.core :as k8s]
            [cheshire.core :as json]
            clojure.pprint
            [clj-service.protocols.config :as config]
            [pitfall.components.kubernetes :as components.kubernetes]))

(defn tap [x]
  (clojure.pprint/pprint x)
  x)

(defn get-mocks-config-map [devspace {:keys [kubernetes config]}]
  (->> {:kind :ConfigMap
        :action :get
        :request {:name      (or (config/get-maybe config :mocks-configmap-name) "fmc-mocks")
                  :namespace devspace}}
       (k8s/invoke (components.kubernetes/client kubernetes))
       tap))

(defn get-mocks [devspace components]
  (->> (get-mocks-config-map devspace components)
       :data
       (map (fn [[k v]] [k (json/parse-string v true)]))
       (into {})))
