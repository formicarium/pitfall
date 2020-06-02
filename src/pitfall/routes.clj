(ns pitfall.routes
  (:require [io.pedestal.http.route :as route]
            [io.pedestal.log :as log]
            [pitfall.config :as config]
            [clojure.pprint :as pprint]
            [clojure.string :as string]
            [pitfall.adapters.mocks :as adapters.mocks]
            [pitfall.logic.mock :as logic.mock]
            [pitfall.controllers.mocks :as controllers.mocks]
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

(defn get-mocks
  [{:keys [path-params components] :as context}]
  {:status 200
   :body (controllers.mocks/get-mocks (:devspace path-params) components)})

(defn catch-all [{:keys [uri request-method server-name components] :as context}]
  (log/info :log :catch-all :uri uri :method request-method :server-name server-name)
  (or (-> (adapters.mocks/mock-opts context)
          (controllers.mocks/mock components)
          :response)
      {:status 501
       :body "Mock not found"}))

(defn patch-mocks
  [{:keys [path-params json-params components] :as context}]
  (log/info :context context)
  (log/info :json json-params)
  {:status 200
   :body (controllers.mocks/patch-mocks (:devspace path-params) json-params components)})

(def routes
  (route/expand-routes
    `[[["/" ^:interceptors [interceptors.error/catch!
                            (body-params/body-params)
                            interceptors.adapt/coerce-body
                            interceptors.adapt/content-neg-intc
                            interceptors.schema/coerce-output]
        {:get [:handle-request handle-request]}
        ["/formicarium/api/version" {:get [:get-version get-version]}]
        ["/formicarium/mocks/:devspace" {:get [:get-mocks get-mocks]
                                         :patch [:patch-mocks patch-mocks]}]
        ["/*path" {:any [:catch-all catch-all]}]]]]))
