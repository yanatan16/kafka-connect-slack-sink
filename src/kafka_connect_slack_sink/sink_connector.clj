(ns kafka-connect-slack-sink.sink-connector
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [kafka-connect-slack-sink.config :refer [config]]
            [kafka-connect-slack-sink.sink-task])
  (:import [kafka.connect.slack.sink SinkTask])
  (:gen-class
   :name kafka.connect.slack.sink.SinkConnector
   :extends org.apache.kafka.connect.sink.SinkConnector
   :state props
   :init init
   :constructors {[] []}
   :prefix "connector-"))

(defn connector-init []
  [[] (atom {})])

(defn connector-taskClass [this] SinkTask)

(defn connector-taskConfigs [this max-tasks]
  (repeat max-tasks @(.props this)))

(defn connector-config [this] config)

(defn connector-start [this props]
  (reset! (.props this) props))

(defn connector-stop [this] nil)

(defn connector-version [this]
  (or (some-> (io/resource "project.clj") slurp edn/read-string (nth 2))
      "unknown"))
