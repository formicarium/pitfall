(ns pitfall.routes
  (:require [io.pedestal.http.route :as route]
            [pitfall.config :as config]
            [clojure.pprint :as pprint]
            [clj-service.pedestal.interceptors.adapt :as interceptors.adapt]
            [clj-service.pedestal.interceptors.schema :as interceptors.schema]
            [clj-service.pedestal.interceptors.error :as interceptors.error]
            [io.pedestal.http.body-params :as body-params]))

(defn get-health [_]
  {:status 200
   :body   {:healthy true}})

(defn get-version
  [{{:keys [config]} :components}]
  {:status 200
   :body   {:version (config/version config)}})

(defn handle-request
  [context]
  (pprint/pprint context)
  {:status 200
   :body   {:hello "BIZZNISS"}})

(def routes
  (route/expand-routes
   `[[["/" ^:interceptors [interceptors.error/catch!
                           (body-params/body-params)
                           interceptors.adapt/coerce-body
                           interceptors.adapt/content-neg-intc
                           interceptors.schema/coerce-output]

       {:get [:handle-request handle-request]}

       ["/healthz" {:get [:get-health get-health]}]

       ["/api"
        ["/version" {:get [:get-version get-version]}]]]]]))
