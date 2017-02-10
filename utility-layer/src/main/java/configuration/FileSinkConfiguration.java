package configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * @Author raphael on 26/01/2017.
 *
 * Class used for the general Kafka properties (topic name, bootstrap servers, zookeeper quorum)
 */
public class FileSinkConfiguration extends GeneralConfiguration {

    private static Logger LOGGER = LogManager.getLogger(FileSinkConfiguration.class);

    // The ELASTICSEARCH_PREFIX is used to get properties from configuration files
    private static final String FILE_PREFIX = "file.";

    // The following string codes are used to create a classic Kafka properties object (see KafkaConfiguration::getKafkaProperties() method)

    private static final String PATH = "path";
    private static final String NAME = "name";
    private static final String EXTENSION = "extension";

    private String path;
    private String name;
    private String extension;

    public FileSinkConfiguration() {
        super();
    }

    public FileSinkConfiguration(String confPath) {
        super(confPath);
    }

    public FileSinkConfiguration(Properties properties) {
        super(properties);
    }

    public FileSinkConfiguration(Properties properties, String prefix) {
        super(properties, prefix);
    }

    @Override
    protected void setAttributesFromProperties() {
        LOGGER.debug("Setting up configuration attributes from properties");
        // Add custom prefix if any and then add "elasticsearch." prefix
        String globalKeyPrefix = this.hasPrefix
                ? this.prefix + FILE_PREFIX
                : FILE_PREFIX;
        this.path = properties.getProperty(globalKeyPrefix + PATH);
        this.name = properties.getProperty(globalKeyPrefix + NAME);
        this.extension = properties.getProperty(globalKeyPrefix + EXTENSION);
        LOGGER.debug("All configuration attributes have been set from properties");
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getFullPath() {
        return this.path + this.name + "." + this.extension;
    }
    @Override
    public String toString() {
        return "FileSinkConfiguration{" + '\n' +
                "\t" + "path='" + path + '\'' + '\n' +
                "\t" + "name='" + name + '\'' + '\n' +
                "\t" + "extension='" + extension + '\'' + '\n' +
                '}';
    }
}
