(ns pitfall.controllers.mocks
  (:require [pitfall.diplomat.kubernetes :as diplomat.kubernetes]
            [pitfall.logic.mock :as logic.mock]))

(defn get-mocks
  [devspace {:keys [kubernetes]}]
  (diplomat.kubernetes/get-mocks devspace))

(defn mock
  [{:keys [method route service devspace]}]
  (logic.mock/find-mock
    ((keyword service) (diplomat.kubernetes/get-mocks devspace))
    {:method method
     :route route}))
