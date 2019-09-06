(ns pitfall.server
  (:require [com.stuartsierra.component :as component]
            [pitfall.components :as pitfall]))

(defn run-dev
  "The entry-point for 'lein run-dev'"
  [& args]
  (prn "Running system map")
  (component/start-system (pitfall/system-map :dev)))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (prn "\nCreating your server...")
  (component/start-system (pitfall/system-map :prod)))
