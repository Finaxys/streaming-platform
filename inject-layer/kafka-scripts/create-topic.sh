#!/bin/bash 
export KAFKA_PATH=/home/finaxys/kafka/kafka_2.10-0.8.2.2

$KAFKA_PATH/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic atom
