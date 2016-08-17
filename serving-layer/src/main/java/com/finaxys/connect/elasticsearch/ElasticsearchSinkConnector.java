package com.finaxys.connect.elasticsearch;


import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Author raphael on 17/08/2016.
 */
public class ElasticsearchSinkConnector extends SinkConnector {

    private static Logger LOGGER = Logger.getLogger(ElasticsearchSinkConnector.class);

    public static final String ELASTIC_HOST = "elastic.host";
    public static final String INDEX_PREFIX = "index.prefix";

    private String elasticHost;
    private String indexPrefix;




    @Override
    public void start(Map<String, String> map) {

    }

    @Override
    public Class<? extends Task> taskClass() {
        return null;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int i) {
        return null;
    }

    @Override
    public void stop() {

    }


    @Override
    public String version() {
        String version = "unknown";
        try {
            Properties props = new Properties();
            props.load(ElasticsearchSinkConnector.class.getResourceAsStream("/elasticsearch-sink.properties"));
            version = props.getProperty("version", version).trim();
        } catch (Exception e) {
            LOGGER.warn("Error while loading version:", e);
        }
        return version;
    }


    @Override
    public ConfigDef config() {
        return null;
    }
}
