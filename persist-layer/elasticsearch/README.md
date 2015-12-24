# Elasticsearch

*TODO*

## Requirements

* Linux
* At least Java 7
* The command line wget

## How to install 

Download the Elasticsearch 2.1.1 tar:
> ```ruby
> wget https://download.elasticsearch.org/elasticsearch/release/org/elasticsearch/distribution/tar/elasticsearch/2.1.1/elasticsearch-2.1.1.tar.gz
> ```

Extract it:
> ```ruby
> tar xvf elasticsearch-2.1.1.tar.gz
> ```

Run a local elasticsearch instance in daemon:
> ```ruby
> elasticsearch-2.1.1/bin/elasticsearch -d
> ```

Check your local Elasticsearch instance through: 

> [http://localhost:9200/](http://localhost:9200/).

Your favorite browser should return a JSON message:
> ```json
> {
>   "name" : "Nekra",
>   "cluster_name" : "elasticsearch",
>   "version" : {
>     "number" : "2.1.1",
>     "build_hash" : "40e2c53a6b6c2972b3d13846e450e66f4375bd71",
>     "build_timestamp" : "2015-12-15T13:05:55Z",
>     "build_snapshot" : false,
>     "lucene_version" : "5.3.1"
>   },
>   "tagline" : "You Know, for Search"
> }
> ```

Your local Elasticsearch instance was launched with a default configuration.

However, you can change the configuration of your ElasticSearch instance through the file:
> ```ruby
> elasticsearch-2.1.1/config/elasticsearch.yml
> ```
