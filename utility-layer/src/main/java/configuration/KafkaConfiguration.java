package configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @Author raphael on 20/12/2016.
 *
 * Class used for the general Kafka properties (topic name, bootstrap servers, zookeeper quorum)
 */
public class KafkaConfiguration extends GeneralConfiguration {

    private static Logger LOGGER = LogManager.getLogger(KafkaConfiguration.class);

    private static final String KAFKA_TOPIC = "kafka.topic";
    private static final String KAFKA_BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";
    private static final String KAFKA_ZOOKEEPER_QUORUM = "kafka.zookeeper.quorum";


    private String kafkaTopic;
    private String kafkaBootstrapServers;
    private String kafkaZookeeperQuorum;

    public KafkaConfiguration() {
        super();
    }


    public KafkaConfiguration(String confPath) {
        super(confPath);
        LOGGER.debug("Creating configuration instance");
    }

    @Override
    protected void setAttributesFromProperties() {
        LOGGER.debug("Setting up configuration attributes from properties");
        this.kafkaTopic = properties.getProperty(KAFKA_TOPIC);
        this.kafkaBootstrapServers = properties.getProperty(KAFKA_BOOTSTRAP_SERVERS);
        this.kafkaZookeeperQuorum = properties.getProperty(KAFKA_ZOOKEEPER_QUORUM);
        LOGGER.debug("All configuration attributes have been set from properties");
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    public void setKafkaBootstrapServers(String kafkaBootstrapServers) {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
    }

    public String getKafkaZookeeperQuorum() {
        return kafkaZookeeperQuorum;
    }

    public void setKafkaZookeeperQuorum(String kafkaZookeeperQuorum) {
        this.kafkaZookeeperQuorum = kafkaZookeeperQuorum;
    }
}
