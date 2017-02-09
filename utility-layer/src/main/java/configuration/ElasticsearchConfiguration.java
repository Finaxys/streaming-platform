package configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * @Author raphael on 26/01/2017.
 *
 * Class used for the general Kafka properties (topic name, bootstrap servers, zookeeper quorum)
 */
public class ElasticsearchConfiguration extends GeneralConfiguration {

    private static Logger LOGGER = LogManager.getLogger(ElasticsearchConfiguration.class);

    // The ELASTICSEARCH_PREFIX is used to get properties from configuration files
    private static final String ELASTICSEARCH_PREFIX = "elasticsearch.";

    // The following string codes are used to create a classic Kafka properties object (see KafkaConfiguration::getKafkaProperties() method)

    private static final String BULK_FLUSH_MAX_ACTIONS = "bulk.flush.max.actions";
    private static final String CLUSTER_NAME = "cluster.name";
    private static final String HOST_ADRESS = "host.adress";
    private static final String HOST_PORT = "host.port";
    private static final String INDEX = "index";
    private static final String DISCRIMINANT = "discriminant";

    private String bulkFlushMaxActions;
    private String clusterName;
    private String hostAdress;
    private Integer hostPort;
    private String index;
    private String discriminant;

    public ElasticsearchConfiguration() {
        super();
    }

    public ElasticsearchConfiguration(String confPath) {
        super(confPath);
    }

    public ElasticsearchConfiguration(Properties properties) {
        super(properties);
    }

    public ElasticsearchConfiguration(Properties properties, String prefix) {
        super(properties, prefix);
    }

    @Override
    protected void setAttributesFromProperties() {
        LOGGER.debug("Setting up configuration attributes from properties");
        // Add custom prefix if any and then add "elasticsearch." prefix
        String globalKeyPrefix = this.hasPrefix
                ? this.prefix + ELASTICSEARCH_PREFIX
                : ELASTICSEARCH_PREFIX;
        this.bulkFlushMaxActions = properties.getProperty(globalKeyPrefix + BULK_FLUSH_MAX_ACTIONS);
        this.clusterName = properties.getProperty(globalKeyPrefix + CLUSTER_NAME);
        this.hostAdress = properties.getProperty(globalKeyPrefix + HOST_ADRESS);
        this.hostPort = Integer.parseInt(properties.getProperty(globalKeyPrefix + HOST_PORT));
        this.index = properties.getProperty(globalKeyPrefix + INDEX);
        this.discriminant = properties.getProperty(globalKeyPrefix + DISCRIMINANT);
        LOGGER.debug("All configuration attributes have been set from properties");
    }


    public String getBulkFlushMaxActions() {
        return bulkFlushMaxActions;
    }

    public void setBulkFlushMaxActions(String bulkFlushMaxActions) {
        this.bulkFlushMaxActions = bulkFlushMaxActions;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getHostAdress() {
        return hostAdress;
    }

    public void setHostAdress(String hostAdress) {
        this.hostAdress = hostAdress;
    }

    public Integer getHostPort() {
        return hostPort;
    }

    public void setHostPort(Integer hostPort) {
        this.hostPort = hostPort;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getDiscriminant() {
        return discriminant;
    }

    public void setDiscriminant(String discriminant) {
        this.discriminant = discriminant;
    }

    @Override
    public String toString() {
        return "ElasticsearchConfiguration{" + '\n' +
                "\t" + "bulkFlushMaxActions='" + bulkFlushMaxActions + '\'' + '\n' +
                "\t" + "clusterName='" + clusterName + '\'' + '\n' +
                "\t" + "hostAdress='" + hostAdress + '\'' + '\n' +
                "\t" + "hostPort='" + hostPort + '\'' + '\n' +
                "\t" + "index='" + index + '\'' + '\n' +
                '}';
    }

    public Map<String, String> getUserConfig() {
        Map<String, String> userConfig = new HashMap<>();
        userConfig.put(BULK_FLUSH_MAX_ACTIONS, this.getBulkFlushMaxActions());
        userConfig.put(CLUSTER_NAME, this.getClusterName());
        return userConfig;
    }

    public List<InetSocketAddress> getTransportAdress() {
        List<InetSocketAddress> transports = new ArrayList<>();
        transports.add(new InetSocketAddress(this.getHostAdress(), this.getHostPort()));
        return transports;
    }
}
