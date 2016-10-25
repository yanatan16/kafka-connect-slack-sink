(defproject kafka-connect-slack-sink "0.1.1"
  :description "Kafka Connector Sink to post to slack via webhook"
  :url "https://github.com/yanatan16/kafka-connect-slack-webhook"
  :license {:name "MIT"
            :url "https://github.com/yanatan16/kafka-connect-slack-webhook/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojars.yanatan16/franzy-connect "0.1.1"]
                 [aleph "0.4.1"]
                 [cheshire "5.6.3"]
                 [de.ubercode.clostache/clostache "1.4.0"]]
  :aot :all

  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["uberjar"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]]

  :uberjar-name "kafka-connect-slack-sink-standalone.jar")
