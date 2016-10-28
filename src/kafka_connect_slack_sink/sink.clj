(ns kafka-connect-slack-sink.sink
  (:require [franzy.connect.sink :as sink]
            [clojure.tools.logging :as log]
            [kafka-connect-slack-sink.config :refer [config]]
            [kafka-connect-slack-sink.slack :as slack]))

(sink/make-sink
 kafka.connect.slack.sink.Sink
 {:start (fn [cfg]
           (log/infof "Starting Slack Sink Task with config: %s" (pr-str cfg))
           cfg)
  :put-1 (fn [props {:keys [value topic key]}]
           (log/infof "Posting kafka record %s" value)
           (slack/post-message props topic key value)
           props)}
 {:config-def config})
