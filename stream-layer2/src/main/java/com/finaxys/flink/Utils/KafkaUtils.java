package com.finaxys.flink.Utils;

import java.util.Properties;

public class KafkaUtils {

    static Properties properties = new Properties();

    public static Properties getProperties() {
        if (properties == null)
            properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
// only required for Kafka 0.8
        properties.setProperty("zookeeper.connect", "localhost:2181");
        properties.setProperty("group.id", "test-consumer-group");
        return properties;
    }

}
