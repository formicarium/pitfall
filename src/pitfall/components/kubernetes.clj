(ns pitfall.components.kubernetes
  (:require [clj-service.protocols.config :as config]
            [kubernetes-api.core :as k8s]
            [com.stuartsierra.component :as component]
            [clojure.pprint :as pprint]
            [clojure.string :as string]))

(defprotocol KubernetesAPI
  "Simple component to keep the k8s-api client"

  (client [this]
    "Returns a k8s-api client"))

(defn new-client [config]
  (k8s/client (or (config/get-maybe config :kubernetes-api-url) "https://kubernetes")
              {:insecure? true
               :token-fn (fn [& _] (string/trim (slurp "/var/run/secrets/kubernetes.io/serviceaccount/token")))}))

(defn start* [this config]
  (assoc this :k8s-client (new-client config)))

(defrecord Kubernetes [config k8s-client]

  component/Lifecycle
  (start [this]
    (start* this config))

  (stop [this]
    this)

  KubernetesAPI
  (client [this] k8s-client)

  Object
  (toString [_]
    "<Kubernetes>"))

(defmethod print-method Kubernetes [x ^java.io.Writer writer]
  (.write writer (str x)))

(defmethod pprint/simple-dispatch Kubernetes [x]
  (.write *out* (str x)))

(defn new-kubernetes []
  (map->Kubernetes {}))

(comment

  (string/trim (slurp "/var/run/secrets/kubernetes.io/serviceaccount/token"))


  (def k8s (k8s/client "https://test-fmc-c-kubernetes-api.nubank.com.br"
                       {:insecure? true
                        :token-fn (fn [& _] (string/trim (slurp "/var/run/secrets/kubernetes.io/serviceaccount/token")))}))

  (def k8s (k8s/client "http://localhost:9000"
                       {:insecure? true}))


  (k8s/info k8s {:kind :ConfigMap
                 :action :get})


  (require '[cheshire.core :as json])

  (->> (k8s/invoke k8s {:kind :ConfigMap
                        :action :get
                        :request {:name "fmc-mocks"
                                  :namespace "default"}})
       :data
       (map (fn [[k v]] [k (json/parse-string v true)]))
       (into {}))

  )
