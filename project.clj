(defproject pitfall "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [leoiacovini/clj-service "1.3.0"]]

  :profiles {:dev     {:aliases      {"run-dev" ["trampoline" "run" "-m" "pitfall.server/run-dev"]}
                       :plugins      [[lein-midje "3.2.1"]]
                       :dependencies [[midje "1.9.1"]
                                      [clj-http-fake "1.0.3"]
                                      [http-kit.fake "0.2.1"]
                                      [nubank/matcher-combinators "0.5.0"]
                                      [nubank/selvage "0.0.1"]]}
             :uberjar {:aot [pitfall.server]}}

  :main ^{:skip-aot true} pitfall.server)
