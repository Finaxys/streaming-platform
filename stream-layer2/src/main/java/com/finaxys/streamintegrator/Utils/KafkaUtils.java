package com.finaxys.streamintegrator.Utils;

import java.util.Properties;

public class KafkaUtils {

    static Properties properties = new Properties();
    static String brokerList = null;
    static String zookeeper = null;
    static String topicAtom = "TopicSAAS";

    public static Properties getProperties() {
        if (properties == null)
            properties = new Properties();
        properties.setProperty("bootstrap.servers", getBrokerList());
        properties.setProperty("group.id", "test-consumer-group");
        return properties;
    }


    public static String getBrokerList() {
        if (brokerList == null)
            brokerList = "broker.kafka.l4lb.thisdcos.directory:9092";
        return brokerList;
    }

    public static String getZookeeper() {
        if (zookeeper == null);
            zookeeper = "localhost:2181";
        return zookeeper;
    }


    public static String getTopicAtom() {
        return topicAtom;
    }
}
