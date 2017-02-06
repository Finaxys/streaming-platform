package com.finaxys.flink.sink;

import configuration.ElasticsearchConfiguration;
import org.apache.flink.streaming.connectors.elasticsearch2.ElasticsearchSink;
import org.apache.flink.streaming.connectors.elasticsearch2.ElasticsearchSinkFunction;

/**
 * @Author raphael on 26/01/2017.
 */
public class ElasticSink<T> extends ElasticsearchSink<T> {

    public static final String ELASTICSEARCH_SINK_KEY = "elasticsearch";

    public ElasticSink(ElasticsearchConfiguration conf, ElasticsearchSinkFunction<T> elasticsearchSinkFunction) {
        super(conf.getUserConfig(), conf.getTransportAdress(), elasticsearchSinkFunction);
    }
}
