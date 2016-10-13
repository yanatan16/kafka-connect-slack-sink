(ns kafka-connect-slack-sink.config
  (:import [org.apache.kafka.common.config
            ConfigDef ConfigDef$Type ConfigDef$Importance]))

(def SLACK_WEBHOOK "slack.webhook")

(def config
  (doto (ConfigDef.)
    (.define SLACK_WEBHOOK ConfigDef$Type/STRING
             ConfigDef/NO_DEFAULT_VALUE ConfigDef$Importance/HIGH
             "Slack Webhook URL")))
