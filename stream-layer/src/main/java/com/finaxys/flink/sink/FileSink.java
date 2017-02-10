package com.finaxys.flink.sink;

import configuration.ElasticsearchConfiguration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch2.ElasticsearchSink;
import org.apache.flink.streaming.connectors.elasticsearch2.ElasticsearchSinkFunction;

/**
 * @Author raphael on 26/01/2017.
 */
public class FileSink {

    public static final String FILE_SINK_KEY = "file";
}
