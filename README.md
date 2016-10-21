# kafka-connect-slack-sink

A kafka sink connector for posting to slack. It has message and username templating, and only requires an incoming webhook to configure!

## Usage

To install into a kafka-connect classpath, simply download an uberjar from the releases page or build it yourself:

```
lein uberjar
cp target/kafka-connect-slack-sink-standalone.jar <destination classpath>
```

Then you can start connectors as normal through the REST API or Confluent's Kafka Control Center.

## Configuration

The `connector.class` is `kafka.connect.slack.sink.SinkConnector`.

It has the following custom configurations (above and beyond the [normal sink configurations](http://docs.confluent.io/2.0.0/connect/userguide.html#configuring-connectors)).

- `slack.webhook` The slack webhook url, required.
- `slack.channel` The slack channel to post to, required, and must start with `#`
- `slack.emoji` The user emoji to post with.
- `slack.username.template` The username to post with, templated (see below)
- `slack.message.template` The message to post, templated (see below)

### Templating

This connector using mustache templating to template usernames and messages. The context of the rendering is:

```json
{
    "topic": "topic-name",
    "key": <arbitrary key>,
    "value": <arbitrary value>
}
```

Some example templates:

```
"{{topic}}" # just the topic name

"{{&value}}" # the arbitrary value in edn form (ampersand is for no escaping)

"User {{key.user_id}} is doing {{value.action}} for {{value.points}} points!"
```

## Testing

Unit tests can be run in the normal clojure way:

```
lein test
```

Integration tests have not been set up yet, but you can set up a test pipeline by getting a slack token and a docker daemon setup:

```
export SLACK_WEBHOOK=<your_webhook>
export SLACK_CHANNEL=<your_channel>
export DOCKER=<docker_host>

# Start the zk/kafka/connect containers (uberjar is important!)
lein uberjar # if you forget this, you might need restart your docker machine
docker-compose up -d

# Now insert things into kafka. I like to use kafkacat
echo '{"user_id":"jon", "thing": "wrote a kafka-connector slack sink for posting messages. <https://github.com/yanatan16/kafka-connect-slack-sink|Check it out>"}' | kafkacat -b $DOCKER:9092 -t test-topic -P

# Prepare the kafka connector config
cat POST.json | \
 sed "s%\${SLACK_WEBHOOK}%${SLACK_WEBHOOK}%" | \
 sed "s%\${SLACK_CHANNEL}%${SLACK_CHANNEL}%" > /tmp/kafka-connect-sink.json

# and send it!
curl $DOCKER:8083/connectors -XPOST -H'Content-type: application/json' -H'Accept: application/json' -d @/tmp/kafka-connect-sink.json

# Now check slack! Your post should be there. If not, look at:
docker-compose logs
```

## License

See [LICENSE](/LICENSE) file.
