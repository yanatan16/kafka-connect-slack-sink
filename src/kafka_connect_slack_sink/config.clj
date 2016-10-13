(ns kafka-connect-slack-sink.config
  (:import [org.apache.kafka.common.config
            ConfigDef ConfigDef$Type ConfigDef$Importance
            ConfigDef$Validator
            ConfigException]))

(def SLACK_WEBHOOK "slack.webhook")
(def SLACK_CHANNEL "slack.channel")
(def SLACK_EMOJI "slack.emoji")
(def SLACK_USERNAME_TEMPLATE "slack.username.template")
(def SLACK_MESSAGE_TEMPLATE "slack.message.template")

(defn validator [f msg]
  (reify ConfigDef$Validator
    (ensureValid [_ name value]
      (if-not (f value)
        (throw (ConfigException. name value msg))))))

(def config
  (doto (ConfigDef.)
    (.define SLACK_WEBHOOK
             ConfigDef$Type/STRING
             ConfigDef/NO_DEFAULT_VALUE
             ConfigDef$Importance/HIGH
             "Slack Webhook URL")
    (.define SLACK_CHANNEL
             ConfigDef$Type/STRING
             ""
             (validator #(or (empty? %) (re-find #"^#" %))
                        "Channel must start with #")
             ConfigDef$Importance/LOW
             "Slack channel. Defaults to none.")
    (.define SLACK_EMOJI
             ConfigDef$Type/STRING
             ""
             (validator #(or (empty? %) (re-find #"^:.*:$" %))
                        "Emoji must start and end with :")
             ConfigDef$Importance/LOW
             "User icon emoji")
    (.define SLACK_USERNAME_TEMPLATE
             ConfigDef$Type/STRING
             ""
             ConfigDef$Importance/LOW
             "Username to post as. If empty, use default. If not empty, templated with mustache and context `'{:topic <topic> :key <key> :value <value>}`.'")
    (.define SLACK_MESSAGE_TEMPLATE
             ConfigDef$Type/STRING
             "{{#key}}{{.}} {{/key}}{{#value}}{{.}}{{/value}}"
             ConfigDef$Importance/HIGH
             "Message to post. Templated with mustache and context `'{:topic <topic> :key <key> :value <value>}`.'")))
