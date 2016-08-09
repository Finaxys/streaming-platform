#!/bin/bash 

# Goto Kafka home directory
cd $KAFKA_HOME # or enter the direct path if you do not have an environment variable


#Start Kafka
# start zookeeper server
./bin/zookeeper-server-start.sh ./config/zookeeper.properties &

# start broker
./bin/kafka-server-start.sh ./config/server.properties &

# create topic named "atomTopic" (name is here as an example)
 ./bin/kafka-topics.sh --create --topic atomTopic --zookeeper localhost:2181 --partitions 1 --replication-factor 1

# consume from the topic using the console producer
./bin/kafka-console-consumer.sh --topic atomTopic --zookeeper localhost:2181

# produce something into the topic (write something and hit enter)
./bin/kafka-console-producer.sh --topic atomTopic --broker-list localhost:9092



# list topics
./bin/kafka-topics.sh --list --zookeeper localhost:2181

# delete a topic
./bin/kafka-topics.sh --delete --zookeeper localhost:2181 --topic your_topic_name

# Stop Kafka instructions (in THIS order)
./bin/kafka-server-stop.sh
./bin/zookeeper-server-stop.sh