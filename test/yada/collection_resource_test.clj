;; Copyright © 2015, JUXT LTD.

(ns yada.collection-resource-test
  (:require
   [clojure.test :refer :all]
   [ring.mock.request :as mock]
   [clojure.java.io :as io]
   [ring.util.time :refer (parse-date format-date)]
   [yada.test.util :refer (given)]
   [yada.yada :refer [yada]]
   yada.collection-resource))

;; Collections can be resources too, we should test them

(deftest map-resource-test
  (testing "atom"
    (let [resource {:name "Frank"}
          handler (yada resource)
          request (mock/request :get "/")
          response @(handler request)
          last-modified (parse-date (-> response :headers (get "last-modified")))]

      (is (instance? java.util.Date last-modified))

      (given response
        :status := 200
        :headers :> {"content-type" "application/edn"
                     "content-length" 16}
        :body :? string?)

      (let [request (merge (mock/request :get "/")
                           {:headers {"if-modified-since" (format-date last-modified)}})
            response @(handler request)
            ]
        #_(given response
          :status := 304))

      )))
