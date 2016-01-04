# Logstash

*TODO*

## Requirements

* Linux
* At least Java 7
* The command line wget

## How to install 

Download the Logstash 2.1.1 tar:
> ```ruby
> wget https://download.elastic.co/logstash/logstash/logstash-2.1.1.tar.gz
> ```

Extract it:
> ```ruby
> tar xvf logstash-2.1.1.tar.gz
> ```

## How to configure Logstash to consume data from Kafka topic and send it to Elasticsearch 

Create a Logstash configuration file named test.config:
> ```ruby
> input {
> 	kafka {
> 		topic_id => "atomTopic"
> 		type => "atom"
> 		codec => "plain"
> 	}
> }
> 
> filter {
> 	if [message] =~ /^Agent/ {
> 		csv {
> 			separator => ";"
> 			columns => ["Trace", "AgentName", "Cash", "ObName", "Executed", "Price", "Ts"]
> 		}
> 
> 		date {
> 			match => ["Ts", "YYYY-MM-dd;HH:mm:ss.SSS", "UNIX_MS"]
> 			target => "@timestamp"
> 		}
> 	} elseif [message] =~ /^Order/ {
> 		csv {
> 			separator => ";"
> 			columns => ["Trace", "ObName", "Sender", "ExtId", "Type", "Id", "Quantity", "Direction", "Price", "Validity", "Ts"]
> 		}
> 
> 		date {
> 			match => ["Ts", "YYYY-MM-dd;HH:mm:ss.SSS", "UNIX_MS"]
> 			target => "@timestamp"
> 		}
> 	}
> 	else {
> 		drop {}
> 	}
> }
> 
> output {
> 	stdout { 
> 		codec => rubydebug 
> 	}
>
> 	elasticsearch { 
> 	}
> }
> ```

## How to run a Logstash agent 

Run a Logstash agent with the configuration file test.config:
> ```ruby
> logstash-2.1.1/bin/logstash agent -f ../config/test.config
> ```
