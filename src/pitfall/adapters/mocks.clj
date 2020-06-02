(ns pitfall.adapters.mocks
  (:require [pitfall.logic.mock :as logic.mock]
            [cheshire.core :as json]))

(defn mock-opts
  [{:keys [request-method uri server-name] :as _context}]
  (merge {:method (name request-method) :route uri} (logic.mock/hostname-info server-name)))

(defn internalize-mocks-config-map [config-map]
  (update config-map
          :data (fn [data]
                  (into {} (map (fn [[k v]] [k (json/parse-string v true)]) data)))))

(defn externalize-mocks-config-map [config-map]
  (update config-map
          :data (fn [data]
                  (into {} (map (fn [[k v]] [k (if (string? v) v (json/generate-string v {:pretty true}))]) data)))))
