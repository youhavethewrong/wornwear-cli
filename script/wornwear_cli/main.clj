(ns wornwear-cli.main
  (:require [babashka.curl :as curl]))

(def search-url "https://reware-production.yerdlesite.com/v4/graphql")
(defn build-item-url
  [id color]
  (format "https://wornwear.patagonia.com/shop/mens/%s/%s" id color))


(defn get-url [url]
  (println "Downloading url:" url)
  (curl/get url))

(defn write-html [file html]
  (println "Writing file:" file)
  (spit file html))

(defn -main [& args]
  (let [[url file] args]
    (when (or (empty? url) (empty? file))
      (println "Usage: <url> <file>")
      (System/exit 1))
    (write-html file (:body (get-url url)))))
