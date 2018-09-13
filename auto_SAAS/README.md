This project is used to install the Big Data services automatically through Ansible.

# On the remote machine
## When "locate" command fails (because the target was recently created or because it is not in the path)
- sudo updatedb

# Ansible
## Retry from a specific task by name
- ansible-playbook playbook.yml --start-at-task="<name>"

## To create a topic:
- bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic TopicSAAS
- bin/kafka-topics.sh --create --zookeeper  10.5.0.6:2181 --replication-factor 1 --partitions 1 --topic TopicSAAS

## To delete a topic:
- bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic TopicSAAS

## To launch a producer :
- bin/kafka-console-producer.sh --broker-list localhost:9092 --topic TopicSAAS
- bin/kafka-console-producer.sh --broker-list  10.5.0.5:9092 --topic TopicSAAS

## To launch a consumer :
- bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic ResultCashByAgents --from-beginning
- bin/kafka-console-consumer.sh --bootstrap-server  10.5.0.5:9092 --topic ResultCashByAgents --from-beginning
- bin/kafka-console-consumer.sh --bootstrap-server  10.5.0.5:9092 --topic ResultMinMaxPrice --from-beginning

## To launch Zookeeper:
- bin/zookeeper-server-start.sh config/zookeeper.properties

## To launch Kafka server :
- bin/kafka-server-start.sh config/server.properties

## To list all the topics :
- bin/kafka-topics.sh --list --zookeeper localhost:2181
- bin/kafka-topics.sh --list --zookeeper  10.5.0.6:2181

## Example of data to send (based on ATOM template)
Agent;Khalil;100;CAT;-7;9;1526462070240
Price;DD;8;9;A;Lucy-10381;Jerome-10231;3;10;1526725026492
Price;DD;8;9;A;Lucy-10381;Jerome-10231;2;11;1526725026492

## To run 
/usr/local/flink/bin/start-local.sh && /usr/local/flink/bin/flink run /opt/private-repo/{{ project_user_}}/target/FinanceFlinkProject-0.1.jar


## Docker commands
- state.savepoints.dir: file:///home/finaxys/StreamingAsAService/output139208798
- sudo docker rm $(sudo docker ps -a -q)
- docker exec -it  bash

