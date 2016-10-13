(ns kafka-connect-slack-sink.slack-test
  (:require [clojure.test :refer [is testing deftest use-fixtures]]
            [aleph.http :as http]
            [manifold.deferred :as d]
            [cheshire.core :as json]
            [clojure.walk :refer [keywordize-keys]]
            [kafka-connect-slack-sink.slack :as slack]
            [kafka-connect-slack-sink.config :as cfg]))

(def PORT 18123)
(def REQUESTS (atom []))
(def BASE_CONFIG {cfg/SLACK_WEBHOOK (str "http://127.0.0.1:" PORT "/webhook")
                  cfg/SLACK_CHANNEL ""
                  cfg/SLACK_EMOJI ""
                  cfg/SLACK_USERNAME_TEMPLATE ""
                  cfg/SLACK_MESSAGE_TEMPLATE "{{value.id}} baz {{value.data}}"})
(def CONFIG (merge BASE_CONFIG
                   {cfg/SLACK_CHANNEL "#test-channel"
                    cfg/SLACK_EMOJI ":rocket:"
                    cfg/SLACK_USERNAME_TEMPLATE "{{topic}}"}))


(defn webhook-handler [req]
  (swap! REQUESTS conj
         (-> req
             (select-keys [:uri :body :request-method :headers])
             (update :body #(-> % slurp json/parse-string keywordize-keys))
             (update :headers select-keys ["content-type"])))
  {:status 200 :body "OK"})

(defn webhook-fixture [f]
  (reset! REQUESTS [])
  (let [server (http/start-server webhook-handler {:port PORT})]
    (f)
    (.close server)))

(use-fixtures :once webhook-fixture)

(deftest message-render-test
  (is (= (slack/render-template "{{topic}}: {{&value}}" "topic" nil {"key" "value"})
         "topic: {:key \"value\"}"))
  (is (= (slack/render-template
          "{{key}}: {{value.headers.id}}"
          "topic" "manhatten" {"headers" {"id" "project"}})
         "manhatten: project"))
  (is (= (slack/render-template
          "{{#key}}{{&key}} {{/key}}{{#value}}{{&value}}{{/value}}"
          "topic" {"a" "key"} {"a" "value"})
         "{:a \"key\"} {:a \"value\"}"))
  (is (= (slack/render-template
          "{{#key}}{{&key}} {{/key}}{{#value}}{{&value}}{{/value}}"
          "topic" nil nil)
         "")))


(deftest basic-usage
  (reset! REQUESTS [])
  (let [res (slack/post-message CONFIG "topic" nil {"id" "foo" "data" "bar"})]
    (is (d/deferred? res))
    @res)
  (let [[req :as reqs] @REQUESTS]
    (is (= 1 (count reqs)))
    (is (= {:uri "/webhook"
            :body {:text "foo baz bar"
                   :username "topic"
                   :icon_emoji ":rocket:"
                   :channel "#test-channel"}
            :request-method :post
            :headers {"content-type" "application/json"}}
           req))))

(deftest no-props-usage
  (reset! REQUESTS [])
  (let [res (slack/post-message BASE_CONFIG "topic" nil {"id" "foo" "data" "bar"})]
    (is (d/deferred? res))
    @res)
  (let [[req :as reqs] @REQUESTS]
    (is (= 1 (count reqs)))
    (is (= {:uri "/webhook"
            :body {:text "foo baz bar"}
            :request-method :post
            :headers {"content-type" "application/json"}}
           req))))
