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
public class OutputSimulationConfiguration extends GeneralConfiguration {

    private static Logger LOGGER = LogManager.getLogger(OutputSimulationConfiguration.class);

    // Outputs
    private boolean outKafka;
    private boolean outFile;
    private String pathToOutputFile;


    public OutputSimulationConfiguration() {
        super();
    }

    public OutputSimulationConfiguration(String pathToConfFile) throws UtilityLayerException {
        super(pathToConfFile);
    }

    public OutputSimulationConfiguration(Properties properties) {
        super(properties);
    }

    public OutputSimulationConfiguration(Properties properties, String prefix) {
        super(properties, prefix);
    }

    @Override
    protected void setAttributesFromProperties() throws UtilityLayerException {
        LOGGER.debug("Setting up configuration attributes from properties");
        getOutputsParameters();
        LOGGER.debug("All configuration attributes have been set from properties");
        LOGGER.debug(this.toString());
    }


    private void getOutputsParameters() {
        LOGGER.debug("Setting up output parameters");
        this.outKafka = Boolean.parseBoolean(properties.getProperty("simul.output.kafka", "false"));
        this.outFile = Boolean.parseBoolean(properties.getProperty("simul.output.file", "false"));
        this.pathToOutputFile = properties.getProperty("simul.output.file.path", "");
    }


    public boolean isOutKafka() {
        return outKafka;
    }

    public void setOutKafka(boolean outKafka) {
        this.outKafka = outKafka;
    }

    public boolean isOutFile() {
        return outFile;
    }

    public void setOutFile(boolean outFile) {
        this.outFile = outFile;
    }

    public String getPathToOutputFile() {
        return pathToOutputFile;
    }

    public void setPathToOutputFile(String pathToOutputFile) {
        this.pathToOutputFile = pathToOutputFile;
    }

    @Override
    public String toString() {
        return "OutputSimulationConfiguration{" + '\n' +
                "\t" + "outKafka=" + outKafka + '\n' +
                "\t" + "outFile=" + outFile + '\n' +
                "\t" + "pathToOutputFile='" + pathToOutputFile + '\'' + '\n' +
                '}';
    }
}
