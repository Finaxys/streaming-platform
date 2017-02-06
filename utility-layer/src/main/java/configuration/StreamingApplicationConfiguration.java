package configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * @Author raphael on 26/01/2017.
 *
 * Class used for the general Kafka properties (topic name, bootstrap servers, zookeeper quorum)
 */
public class StreamingApplicationConfiguration extends GeneralConfiguration {

    private static Logger LOGGER = LogManager.getLogger(StreamingApplicationConfiguration.class);

    public static final String SOURCE_PREFIX = "source.";
    public static final String SINK_PREFIX = "sink.";

    // The APPLICATION_PREFIX is used to get properties from configuration file
    private static final String APPLICATION_PREFIX = "application.";

    // The following string codes are used to create a classic Kafka properties object (see KafkaConfiguration::getKafkaProperties() method)
    private static final String PROCESSOR_NAME = "processor.name";
    private static final String SOURCE_TYPE = "source.type";
    private static final String SINK_TYPE = "sink.type";

    private String processorName;
    private String sourceType;
    private String sinkType;


    public StreamingApplicationConfiguration() {
        super();
    }

    public StreamingApplicationConfiguration(String confPath) {
        super(confPath);
    }

    public StreamingApplicationConfiguration(Properties properties) {
        super(properties);
    }

    public StreamingApplicationConfiguration(Properties properties, String prefix) {
        super(properties, prefix);
    }

    @Override
    protected void setAttributesFromProperties() {
        LOGGER.debug("Setting up configuration attributes from properties");
        // Add custom prefix if any and then add "elasticsearch." prefix
        String globalKeyPrefix = this.hasPrefix
                ? this.prefix + APPLICATION_PREFIX
                : APPLICATION_PREFIX;
        this.processorName= properties.getProperty(globalKeyPrefix + PROCESSOR_NAME);
        this.sourceType= properties.getProperty(globalKeyPrefix + SOURCE_TYPE);
        this.sinkType= properties.getProperty(globalKeyPrefix + SINK_TYPE);
        LOGGER.debug("All configuration attributes have been set from properties");
    }

    public String getProcessorName() {
        return processorName;
    }

    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSinkType() {
        return sinkType;
    }

    public void setSinkType(String sinkType) {
        this.sinkType = sinkType;
    }

    @Override
    public String toString() {
        return "StreamingApplicationConfiguration{" + '\n' +
                "\t" + "processorName='" + processorName + '\'' + '\n' +
                "\t" + "sourceType='" + sourceType + '\'' + '\n' +
                "\t" + "sinkType='" + sinkType + '\'' + '\n' +
                '}';
    }


    public static enum SOURCES_TYPES {
        KAFKA("kafka");
        String name;
        SOURCES_TYPES(String name) {this.name = name;}
        public String getName() {return name;}
    }


    public static enum SINK_TYPES {
        ELASTICSEARCH("elasticsearch");
        String name;
        SINK_TYPES(String name) {this.name = name;}
        public String getName() {return name;}
    }



}
