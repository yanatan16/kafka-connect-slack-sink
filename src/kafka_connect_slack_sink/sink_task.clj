(ns kafka-connect-slack-sink.sink-task
  (:require [clj-http.client :as http]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [kafka-connect-slack-sink.config :as cfg])
  (:gen-class
   :name kafka.connect.slack.sink.SinkTask
   :extends org.apache.kafka.connect.sink.SinkTask
   :state cfg
   :init init
   :constructors {[] []}
   :prefix "task-"))

(defn task-init []
  [[] (atom {})])

(defn task-start [this props]
  (reset! (.cfg this) {:webhook (get props cfg/SLACK_WEBHOOK)}))

(defn task-put [this records]
  (let [slack-webhook-url (:webhook @(.cfg this))]
    (doall
     (for [record records]
       (http/post slack-webhook-url
                  {:content-type :json
                   :form-params {:text (pr-str (.value record))
                                 :username (.topic record)}})))))

(defn task-flush [this] nil)

(defn task-stop [this] nil)

(defn task-version [this]
  (or (some-> (io/resource "project.clj") slurp edn/read-string (nth 2))
      "unknown"))
