(ns pitfall.logic.mock-test
  (:require [clojure.test :refer :all]
            [pitfall.logic.mock :as mock]))

(deftest find-mock-test
  (is (= {:request {:route "/api/version"
                    :method "get"}
          :response {:status 200
                     :body {:version "99"}}}
         (mock/find-mock
           {:mocks [{:request {:route "/api/version"
                               :method "get"}
                     :response {:status 200
                                :body {:version "99"}}}
                    {:request {:route "/other/request"
                               :method "get"}
                     :response {:status 200
                                :body {:other 42}}}]}
           {:method "get"
            :route "/api/version"}))))


(deftest hostname-info-test
  (is (= {:service "auth"
          :devspace "lucas"}
         (mock/hostname-info "auth.lucas.top-level-domain.com"))))

(comment

  (mock/hostname-info "auth.lucas.top-level-domain.com")

  (mock/find-mock
    {:mocks [{:request {:route "/api/version"
                        :method "get"}
              :response {:status 200
                         :body {:version "99"}}}
             {:request {:route "/other/request"
                        :method "get"}
              :response {:status 200
                         :body {:other 42}}}]}
    {:method "get"
     :route "/api/version"})

  )
