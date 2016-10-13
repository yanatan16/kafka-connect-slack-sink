(ns kafka-connect-slack-sink.sink-task
  (:require [clj-http.client :as http]
            [kafka-connect-slack-sink.config :as cfg])
  (:gen-class
   :name kafka-connect-slack-sink.SinkTask
   :extends org.apache.kafka.connect.sink.SinkTask
   :state props
   :init init
   :constructors [[] []]))

(defn -start [this props]
  (set! (.props this) props))

(defn -put [this records]
  (let [slack-webhook-url (get props cfg/SLACK_WEBHOOK_URL)]
    (doall
     (for [record records]
       (http/post slack-webhook-url
                  {:content-type :json
                   :form-params {:text (pr-str (.value record))
                                 :username (.topic record)}})))))

(defn -flush [this] nil)

(defn -stop [this] nil)

(defn -version [this]
  (or (some-> (io/resource "project.clj") slurp edn/read-string (nth 2))
      "unknown"))
