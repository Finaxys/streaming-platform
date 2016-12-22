package configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.UtilityLayerException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @Author raphael on 20/12/2016.
 *
 * Class used to load properties from a given file (full path)
 */
public abstract class GeneralConfiguration {

    private static final Logger LOGGER = LogManager.getLogger(GeneralConfiguration.class);
    protected Properties properties;


    /**
     * Generate en empty configuration Object : all sub-classes attributes will need to be set
     * manually.
     *
     * May be useful for tests.
     */
    public GeneralConfiguration() {
        this.properties = new Properties();
    }

    /**
     * Create a GeneralConfiguration, load configuration from a file and set the class
     * local attributes with the values found in the configuration file.
     *
     * @param pathToConfFile The absolute path to the file containing the configuration
     * @throws UtilityLayerException if the file can'f be found
     */
    public GeneralConfiguration(String pathToConfFile) throws UtilityLayerException {
        this.loadFromFile(pathToConfFile);
        setAttributesFromProperties();
    }


    /**
     * Load the properties file configuration into the Properties attribute
     * @param pathToConfFile The absolute path to the file containing the configuration
     */
    public void loadFromFile(String pathToConfFile) {
        properties = new Properties();

        try {
            properties.load(new FileInputStream(pathToConfFile));
        } catch (IOException e) {
            LOGGER.error("Not able to load properties from file " + pathToConfFile);
            throw new UtilityLayerException(e.getMessage());
        }
    }

    /**
     * Set local class attributes with the values contained in the configuration file.
     *
     * Method is abstract and must be redefined in sub-classes
     */
    abstract protected void setAttributesFromProperties();

}