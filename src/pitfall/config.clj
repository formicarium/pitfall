(ns pitfall.config
  (:require [clj-service.protocols.config :as protocols.config]
            [schema.core :as s]))

(s/defn version :- s/Str
  [config :- protocols.config/IConfig]
  (protocols.config/get! config :version))
