package configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * @Author raphael on 20/12/2016.
 *
 * Class used for the general Kafka properties (topic name, bootstrap servers, zookeeper quorum)
 */
public class KafkaConfiguration extends GeneralConfiguration {

    private static Logger LOGGER = LogManager.getLogger(KafkaConfiguration.class);
    private static final String KAFKA_PREFIXE = "kafka.";

    private static final String TOPIC = "topic";
    private static final String BOOTSTRAP_SERVERS = "bootstrap.servers";
    private static final String ZOOKEEPER_QUORUM = "zookeeper.quorum";
    private static final String GROUP_ID = "group.id";


    private String kafkaTopic;
    private String kafkaBootstrapServers;
    private String kafkaZookeeperQuorum;

    public KafkaConfiguration() {
        super();
    }


    public KafkaConfiguration(String confPath) {
        super(confPath);
    }

    @Override
    protected void setAttributesFromProperties() {
        LOGGER.debug("Setting up configuration attributes from properties");
        this.kafkaTopic = properties.getProperty(KAFKA_PREFIXE + TOPIC);
        this.kafkaBootstrapServers = properties.getProperty(KAFKA_PREFIXE + BOOTSTRAP_SERVERS);
        this.kafkaZookeeperQuorum = properties.getProperty(KAFKA_PREFIXE + ZOOKEEPER_QUORUM);
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

    public Properties getKafkaProperties() {
        Properties p = new Properties();
        p.setProperty(BOOTSTRAP_SERVERS, properties.getProperty(KAFKA_PREFIXE + BOOTSTRAP_SERVERS));
        p.setProperty(ZOOKEEPER_QUORUM, properties.getProperty(KAFKA_PREFIXE + ZOOKEEPER_QUORUM));
        return p;
    }

    public Properties getKafkaPropertiesWithGroupID(String groupId) {
        Properties p = new Properties();
        p.setProperty(BOOTSTRAP_SERVERS, properties.getProperty(KAFKA_PREFIXE + BOOTSTRAP_SERVERS));
        p.setProperty(ZOOKEEPER_QUORUM, properties.getProperty(KAFKA_PREFIXE + ZOOKEEPER_QUORUM));
        p.setProperty(GROUP_ID, groupId);
        return p;
    }
}
