(ns wornwear-cli.main
  (:require [babashka.curl :as curl]
            [cheshire.core :as json]
            [spartan.spec] ;; supply clojure.spec.alpha for venia
            [venia.core :as v]))

(def search-url "https://reware-production.yerdlesite.com/v4/graphql")

(defn build-item-url
  [id color]
  (format "https://wornwear.patagonia.com/shop/mens/%s/%s" id color))

(def mens-medium [:partner {:uuid "7d32ad83-330e-4ccc-ba03-3bb32ac113ac"}
                  [[:shop {:slug "mens"}
                    [[:browse {:limit 100 :sort ""
                               :filters [{:name "M" :tag "size"}]}
                      [[:items [:availableSizes :color :displayColor
                                :price :title
                                [:pdpLink [:path :url]]]]]]]]]])

(def ww-query
  (v/graphql-query {:venia/operation {:operation/type :query
                                      :operation/name "initShop"}
                    :venia/queries   [{:query/data mens-medium}]}))

(def standard-params
  {:body (json/generate-string {:query ww-query})
   :headers {"Accept-Language" "en-US,en;q=0.5"
             "Content-Type" "application/json"
             "Referer" "https://wornwear.patagonia.com/shop/mens"
             "origin" "https://wornwear.patagonia.com"
             "DNT" "1"
             "Connection" "keep-alive"}})

(defn post-search
  [url]
  (:body (curl/post url standard-params)))

(defn -main [& _args]
  (println "Posting search")
  (let [data (json/parse-string (post-search search-url) true)]
    (doseq [item (:items (get-in data [:data :partner :shop :browse]))]
      (println item))))
