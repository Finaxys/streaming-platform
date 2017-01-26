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

    // The KAFKA_PREFIX is used to get properties from configuration files
    private static final String KAFKA_PREFIX = "kafka.";

    // The following string codes are used to create a classic Kafka properties object (see KafkaConfiguration::getKafkaProperties() method)
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

    public KafkaConfiguration(Properties properties) {
        super(properties);
    }

    @Override
    protected void setAttributesFromProperties() {
        LOGGER.debug("Setting up configuration attributes from properties");
        // Add custom prefix if any and add "kafka." prefix
        String globalKeyPrefix = this.hasPrefix
                ? this.prefix + KAFKA_PREFIX
                : KAFKA_PREFIX;
        this.kafkaTopic = properties.getProperty(globalKeyPrefix + TOPIC);
        this.kafkaBootstrapServers = properties.getProperty(globalKeyPrefix + BOOTSTRAP_SERVERS);
        this.kafkaZookeeperQuorum = properties.getProperty(globalKeyPrefix + ZOOKEEPER_QUORUM);
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
        p.setProperty(BOOTSTRAP_SERVERS, kafkaBootstrapServers);
        p.setProperty(ZOOKEEPER_QUORUM, kafkaZookeeperQuorum);
        return p;
    }

    public Properties getKafkaPropertiesWithCustomGroupID(String groupId) {
        Properties p = new Properties();
        p.setProperty(BOOTSTRAP_SERVERS, kafkaBootstrapServers);
        p.setProperty(ZOOKEEPER_QUORUM, kafkaZookeeperQuorum);
        p.setProperty(GROUP_ID, groupId);
        return p;
    }

    @Override
    public String toString() {
        return "KafkaConfiguration{" + '\n' +
                "\t" + "kafkaTopic='" + kafkaTopic + '\'' + '\n' +
                "\t" + "kafkaBootstrapServers='" + kafkaBootstrapServers + '\'' + '\n' +
                "\t" + "kafkaZookeeperQuorum='" + kafkaZookeeperQuorum + '\'' + '\n' +
                '}';
    }
}
