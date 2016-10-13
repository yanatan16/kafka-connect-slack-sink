(ns kafka-connect-slack-sink.sink-task
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [kafka-connect-slack-sink.config :as cfg]
            [kafka-connect-slack-sink.slack :as slack]
            [clojure.walk :refer [keywordize-keys]])
  (:import [java.util Map List Set])
  (:gen-class
   :name kafka.connect.slack.sink.SinkTask
   :extends org.apache.kafka.connect.sink.SinkTask
   :state props
   :init init
   :constructors {[] []}
   :prefix "task-"))

(defn convert-schemaless-object [^Object o]
  (cond
    (instance? Map o) (zipmap (.keySet o) (map convert-schemaless-object (.values o)))
    (instance? List o) (vec (map convert-schemaless-object o))
    (instance? Set o) (set (map convert-schemaless-object o))
    (string? o) (str o)
    :else o))

(defn task-init []
  [[] (atom {})])

(defn task-start [this props]
  (reset! (.props this) props)
  (log/infof "Starting slack-sink task with props %s" (pr-str props)))

(defn task-put [this records]
  (doseq [record records
          :let [key (convert-schemaless-object (.key record))
                value (convert-schemaless-object (.value record))]]
    (do (log/infof "Posting kafka record %s" value)
        (slack/post-message @(.props this) (.topic record) key value))))

(defn task-flush [this offsets] nil)

(defn task-stop [this] nil)

(defn task-version [this]
  (or (some-> (io/resource "project.clj") slurp edn/read-string (nth 2))
      "unknown"))
