(ns pitfall.controllers.mocks
  (:require [pitfall.diplomat.kubernetes :as diplomat.kubernetes]
            [pitfall.logic.mock :as logic.mock]))

(defn get-mocks
  [devspace components]
  (diplomat.kubernetes/get-mocks devspace components))

(defn mock
  [{:keys [method route service devspace]} components]
  (logic.mock/find-mock
    ((keyword service) (diplomat.kubernetes/get-mocks devspace components))
    {:method method
     :route route}))
