package com.finaxys.loging.injectors;


import configuration.KafkaConfiguration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Properties;

/**
 * Class that will insert a given String or a list of String in a given Kafka topic
 */
public class KafkaInjector extends AtomDataInjector {

    private static Logger LOGGER = LogManager.getLogger(KafkaInjector.class);

    private Producer<String, String> producer;
    private KafkaConfiguration kafkaConf;
    private String topic;
    private Long count = 0L;

    public KafkaInjector(KafkaConfiguration kafkaConf) {
        this.kafkaConf = kafkaConf;
    }

    @Override
    public void createOutput() {
        topic = kafkaConf.getKafkaTopic();
        producer = createKafkaProducerFromConf(kafkaConf);
        LOGGER.debug("Output successfully created");
    }

    @Override
    public void closeOutput() {
        producer.close();
        LOGGER.debug(count + " messages sent");
        LOGGER.debug("Output successfully closed");
    }

    private KafkaProducer<String, String> createKafkaProducerFromConf(KafkaConfiguration kafkaConf) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConf.getKafkaBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put("request.timeout.ms", 100);
        return new KafkaProducer<>(props);
    }

    @Override
    public void send(String message) {
        producer.send(new ProducerRecord<String, String>(topic, (++count).toString(), message));
    }
}
