FROM confluentinc/cp-kafka-connect:3.0.1

RUN mkdir -p /usr/share/java/kafka-connect-slack-sink
ADD target/kafka-connect-slack-sink-0.1.0-SNAPSHOT-standalone.jar /usr/share/java/kafka-connect-slack-sink/standalone.jar
