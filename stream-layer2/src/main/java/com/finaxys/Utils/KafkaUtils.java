package com.finaxys.Utils;

import java.util.Properties;

public class KafkaUtils {

    static Properties properties = new Properties();
    static String brokerList = "172.20.0.3:9092";
    static String zookeeper = "172.20.0.2:2181";

    public static Properties getProperties() {
        if (properties == null)
            properties = new Properties();
        properties.setProperty("bootstrap.servers", brokerList);
// only required for Kafka 0.8
        properties.setProperty("zookeeper.connect", zookeeper);
        properties.setProperty("group.id", "test-consumer-group");
        return properties;
    }


    public static String getBrokerList() {
        if (brokerList == null)
            brokerList = "172.20.0.3:9092";
        return brokerList;
    }

    public static String getZookeeper() {
        if (zookeeper == null)
            zookeeper = "172.20.0.2:2181";
        return zookeeper;
    }

}
