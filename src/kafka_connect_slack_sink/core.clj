(ns kafka-connect-slack-sink.core
  (:require [kafka-connect-slack-sink
             [slack :as slack]])
  (:import [org.apache.kafka.connect.sink SinkConnector]))

(proxy [SinkConnector] [props']
  (taskClass [this] (.class this))
  (taskConfigs [this max-tasks]
    (repeat max-tasks @props'))
  (config [this] config-def)
  (start [this props]
    (reset! props' props))
  (stop [this] nil))
