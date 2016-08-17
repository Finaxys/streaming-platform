#!/bin/bash 

# Goto Kafka home directory
cd $KAFKA_HOME # or enter the direct path if you do not have an environment variable


#Start Kafka
# start zookeeper server
$KAFKA_HOME/bin/zookeeper-server-start.sh ./config/zookeeper.properties &

# start broker
$KAFKA_HOME/bin/kafka-server-start.sh ./config/server.properties &

# create topic named "atom-raw-data" (name is here as an example)
$KAFKA_HOME/bin/kafka-topics.sh --create --topic atom-raw-data --zookeeper localhost:2181 --partitions 1 --replication-factor 1

# consume from the topic using the console producer
$KAFKA_HOME/bin/kafka-console-consumer.sh --topic atom-raw-data --zookeeper localhost:2181

# produce something into the topic (write something and hit enter)
$KAFKA_HOME/bin/kafka-console-producer.sh --topic atom-raw-data --broker-list localhost:9092



# list topics
$KAFKA_HOME/bin/kafka-topics.sh --list --zookeeper localhost:2181

# delete a topic
$KAFKA_HOME/bin/kafka-topics.sh --delete --zookeeper localhost:2181 --topic your_topic_name

# Stop Kafka instructions (in THIS order)
$KAFKA_HOME/bin/kafka-server-stop.sh
$KAFKA_HOME/bin/zookeeper-server-stop.sh