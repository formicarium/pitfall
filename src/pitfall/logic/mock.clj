(ns pitfall.logic.mock
  (:require [clojure.string :as string]))

(defn find-mock
  [service-mocks {:keys [route method] :as _pitfall-request}]
  (->> (:mocks service-mocks)
       (filter (fn [{:keys [request response]}]
                 (and (= method (:method request))
                      (= route (:route request)))))
       first))


(defn hostname-info [server-name]
  (let [[service devspace] (string/split server-name #"\.")]
    {:service service
     :devspace devspace}))
