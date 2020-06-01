(ns pitfall.server
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [pitfall.components :as pitfall]))

(defn run-dev
  "The entry-point for 'lein run-dev'"
  [& args]
  (component/start-system (pitfall/system-map :dev)))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (component/start-system (pitfall/system-map :prod)))
