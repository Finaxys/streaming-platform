package com.finaxys.streamintegrator.Utils;

import java.util.Properties;

public class KafkaUtils {

    static Properties properties = new Properties();
    //static String brokerList = "10.5.0.5:9092";
    static String brokerList = "localhost:9092";
    //static String zookeeper = "10.5.0.6:2181";
    static String zookeeper = "localhost:2181";
    static String topicAtom = "TopicSAAS";

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
            //brokerList = "10.5.0.5:9092";
            brokerList = "localhost:9092";
        return brokerList;
    }

    public static String getZookeeper() {
        if (zookeeper == null)
            //zookeeper = "10.5.0.6:2181";
            zookeeper = "localhost:2181";
        return zookeeper;
    }


    public static String getTopicAtom() {
        return topicAtom;
    }
}
