(ns kafka-connect-slack-sink.config
  (:require [franzy.connect.config :refer [make-config-def] :as cfg]))

(def SLACK_WEBHOOK "slack.webhook")
(def SLACK_CHANNEL "slack.channel")
(def SLACK_EMOJI "slack.emoji")
(def SLACK_USERNAME_TEMPLATE "slack.username.template")
(def SLACK_MESSAGE_TEMPLATE "slack.message.template")

(def channel-validator
  (cfg/validator #(or (empty? %) (re-find #"^#" %))
                 "Channel must start with #"))

(def emoji-validator
  (cfg/validator #(or (empty? %) (re-find #"^:.*:$" %))
                 "Emoji must start and end with :"))

(def config
  (make-config-def
   (:slack.webhook :type/string ::cfg/no-default-value :importance/high
                   "Slack Webhook URL")
   (:slack.channel :type/string "" channel-validator :importance/low
                   "Slack channel. Deafults to none")

   (:slack.emoji :type/string "" :importance/low
                 "User icon emoji")
   (:slack.username.template
    :type/string "" :importance/low
    "Username to post as. If empty, use default. If not empty, templated with mustache and context `'{:topic <topic> :key <key> :value <value>}`.'")
   (:slack.message.template
    :type/string
    "{{#key}}{{.}} {{/key}}{{#value}}{{.}}{{/value}}"
    :importance/high
    "Message to post. Templated with mustache and context `'{:topic <topic> :key <key> :value <value>}`.'")))
