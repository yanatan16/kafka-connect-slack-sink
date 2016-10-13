(defproject kafka-connect-slack-sink "0.1.0-SNAPSHOT"
  :description "Kafka Connector Sink to post to slack via webhook"
  :url "https://github.com/yanatan16/kafka-connect-slack-webhook"
  :license {:name "MIT"
            :url "https://github.com/yanatan16/kafka-connect-slack-webhook/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "3.3.0"]
                 [org.apache.kafka/connect-api "0.10.0.1"]]
  :aot :all)
