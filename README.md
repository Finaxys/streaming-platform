# Streaming platform

How to ingest million of transactions every day and produce accurate reporting in near real-time? This is the challenge that we face nowadays in Financial industry. 

Front-Office, Risk Management and Compliance used to be separate systems with different workload: online interaction and real-time analytics in intraday on a side, batch and consolidated reporting on the other.

Things as change, regulatory report and risk monitoring needs to be perform in intraday. Trading desk are looking for opportunities in by having a 360 vision of their customers.

Technologies such Big Data and Reactive sytems offer now the possibilities to build innovative Financial Platform to face all these challlenges.

This project aims to build a unify solution that offers a way to produce intraday report based on streaming data in reactive manner. The platform use an event driven architecture with Big Data components such as HBase, Spark, Kakfa, Elasticsearch and so on...

![streaming-platform-architecture.jpg](images/streaming-platform-architecture.jpg)

## How to contribute

### Slack 
We will use a collaborative communication platform to facilitate the discussion on this project. 
To join the team Finaxys Slack, you must register through the link https://finaxys.slack.com/signup

### Trello
We will use a project management tool online to manage the project development and the different steps of completion.
To view the Trello board of the project https://trello.com/b/2o0uBcrB/finaxys-streaming-platform, you must:
* Have a Trello account
* Have access to the project board (ask access to adouang on Slack)

### Our cluster

* *Hosts*
    - master01.cl02.sr.x2p.fr 195.154.134.135 finaxys 
    - worker01.cl02.sr.x2p.fr 195.154.134.136 finaxys1 
    - worker02.cl02.sr.x2p.fr 195.154.134.107 finaxys1 
* *Web UI*
    - Ambari interface : master01.cl02.sr.x2p.fr:8080 admin
    - HDFS interface : master01.cl02.sr.x2p.fr:50070
    - HBase interface : master01.cl02.sr.x2p.fr:60010
    - YARN interface : worker01.cl02.sr.x2p.fr:8088
    - Spark interface : worker01.cl02.sr.x2p.fr:18080

### Add ALM Finaxys certificat to your JDK

Download the certificat: https://alm.finaxys.com/ALMsite/cacert.alm.finaxys.com.cer

Add the previous certificat downloaded to your java trust store.
>```ruby
>sudo keytool -importcert -file cacert.alm.finaxys.com.cer -keystore $JAVA_HOME/jre/lib/security/cacerts -trustcacerts
>```



## Run instructions (work in progress)

```bash
##################  KAFKA  ##################

# start zookeeper server
$KAFKA_HOME/bin/zookeeper-server-start.sh ./config/zookeeper.properties
# start broker
$KAFKA_HOME/bin/kafka-server-start.sh ./config/server.properties

# list topics
$KAFKA_HOME/bin/kafka-topics.sh --list --zookeeper localhost:2181

# if "atom-raw-data" topic does not exist, create it
# create topic named "atom-raw-data"
$KAFKA_HOME/bin/kafka-topics.sh --create --topic atom-raw-data --zookeeper localhost:2181 --partitions 1 --replication-factor 1

# In another terminal, suscribe to topic to monitor
# consume from the topic using the console producer
$KAFKA_HOME/bin/kafka-console-consumer.sh --topic atom-raw-data --zookeeper localhost:2181


##################  ELASTICSEARCH  ##################

# In a separated terminal, run an elastic instance (or run it in the background with & if you want) 
$ELASTIC_HOME/bin/elasticsearch

# Create indexes if they do not exists
curl -XPUT 'localhost:9200/atom-raw-data'
curl -XPUT 'localhost:9200/keep-agent-order-exec'
# Command to list indexes : 
curl 'localhost:9200/_cat/indices?v'



##################  LOGSTASH  ##################

# Launch Logstash that will subscribe to the kafka topic
# Use raw-data.config or keep-agent-order-exec.config or both
$LOGSTASH_HOME/bin/logstash agent -f index-layer/logstash/config/keep-agent-order-exec.config
$LOGSTASH_HOME/bin/logstash agent -f index-layer/logstash/config/atom-raw-data.config



##################  KIBANA  ##################

# Run the kibana server
$KIBANA_HOME/bin/kibana
# Go to http://localhost:5601/app/kibana to see results in Kibana



##################  Run Atom Generate  ##################

# Finally, run the class AtomGenerate that will write Atom simulation data into the Kafka 
# topic named "atom-raw-data" 
```



## STOP ALL SERVICES


```bash
# Go through your terminal window and kill with CTRL+C in this order
#   - Kibana
#   - Logstash instances
#   - ElasticSearch
#   - Kafka Consumer
#       - Kafka Consumer

# Finally stop Kafka Server and Zookeeper (in this order)
$KAFKA_HOME/bin/kafka-server-stop.sh
$KAFKA_HOME/bin/zookeeper-server-stop.sh
```

