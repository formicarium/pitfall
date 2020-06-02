(ns pitfall.adapters.mocks
  (:require [pitfall.logic.mock :as logic.mock]
            ))

(defn mock-opts
  [{:keys [request-method uri server-name] :as _context}]
  (merge {:method (name request-method) :route uri} (logic.mock/hostname-info server-name)))
