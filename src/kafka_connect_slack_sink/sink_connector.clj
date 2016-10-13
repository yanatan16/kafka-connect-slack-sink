(ns kafka-connect-slack-sink.sink-connector
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [kafka-connect-slack-sink.config :refer [config]])
  (:import [kafka-connect-slack-sink SinkTask])
  (:gen-class
   :name kafka-connect-slack-sink.SinkConnector
   :extends org.apache.kafka.connect.sink.SinkConnector
   :state props
   :init init
   :constructors [[] []]))

(defn -init []
  [[] (ref {})])

(defn -task-class [this]
  (.-class SinkTask))

(defn -task-configs [this max-tasks]
  (repeat max-tasks (.props this)))

(defn -config [this] config)

(defn -start [this props]
  (set! (.props this) props))

(defn -stop [this] nil)

(defn -version [this]
  (or (some-> (io/resource "project.clj") slurp edn/read-string (nth 2))
      "unknown"))
