package configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import utils.UtilityLayerException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


/**
 * Main configuration class that holds all properties for the ATOM simulation.
 */
public class DelaySimulationConfiguration extends GeneralConfiguration {

    private static Logger LOGGER = LogManager.getLogger(DelaySimulationConfiguration.class);

    private int outOfOrderPercentage;
    private int outOfOrderMaxDelayInSeconds;

    public DelaySimulationConfiguration() {
        super();
    }

    public DelaySimulationConfiguration(String pathToConfFile) throws UtilityLayerException {
        super(pathToConfFile);
    }

    public DelaySimulationConfiguration(Properties properties) {
        super(properties);
    }

    public DelaySimulationConfiguration(Properties properties, String prefix) {
        super(properties, prefix);
    }

    @Override
    protected void setAttributesFromProperties() throws UtilityLayerException {
        LOGGER.debug("Setting up configuration attributes from properties");
        this.outOfOrderPercentage = Integer.parseInt(properties.getProperty("simul.time.outOfOrder.percentage", "0"));
        this.outOfOrderMaxDelayInSeconds = Integer.parseInt(properties.getProperty("simul.time.outOfOrder.maxDelayInSeconds", "0"));
        LOGGER.debug("All configuration attributes have been set from properties");
        LOGGER.debug(this.toString());
    }


    public int getOutOfOrderPercentage() {
        return outOfOrderPercentage;
    }

    public void setOutOfOrderPercentage(int outOfOrderPercentage) {
        this.outOfOrderPercentage = outOfOrderPercentage;
    }

    public int getOutOfOrderMaxDelayInSeconds() {
        return outOfOrderMaxDelayInSeconds;
    }

    public long getOutOfOrderMaxDelayInMillies() {
        return outOfOrderMaxDelayInSeconds * TimeUnit.SECONDS.toMillis(1);
    }

    public void setOutOfOrderMaxDelayInSeconds(int outOfOrderMaxDelay) {
        this.outOfOrderMaxDelayInSeconds = outOfOrderMaxDelay;
    }


    @Override
    public String toString() {
        return "DelaySimulationConfiguration{" + '\n' +
                "\t" + "outOfOrderPercentage=" + outOfOrderPercentage + '\n' +
                "\t" + "outOfOrderMaxDelayInSeconds=" + outOfOrderMaxDelayInSeconds + '\n' +
                '}';
    }
}
