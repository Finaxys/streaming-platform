package configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import utils.UtilityLayerException;

/**
 * Main configuration class that holds all properties for the ATOM simulation.
 */
public class AtomSimulationConfiguration extends GeneralConfiguration {

    private static Logger LOGGER = LogManager.getLogger(AtomSimulationConfiguration.class);

    // Business Data
    private String agentsParam;
    private List<String> agents;
    private String orderBooksParam;
    private List<String> orderBooks;

    private int orderBooksRandom;
    private int agentsRandom;

    private int tickOpening;
    private int tickContinuous;
    private int tickClosing;
    private int days;

    private boolean marketMarker;
    private int marketMakerQuantity;

    // Outputs
    private boolean outKafka;
    private boolean outFile;
    private String pathToOutputFile;

    // App data


    private int agentCash;
    private int agentMinPrice;
    private int agentMaxPrice;
    private int agentMinQuantity;
    private int agentMaxQuantity;

    // AtomTimeStampBuilder
    private String tsbTimeZone;
    private String tsbDateBegin;
    private String tsbOpenHour;
    private String tsbCloseHour;
    private int nbAgents;
    private int nbOrderBooks;
    private boolean timestampEnabled;
    private boolean timestampHumanReadableEnabled;
    private String dateFormat;
    private String timeFormat;
    private int outOfOrderCoefficient;
    private int outOfOrderPercentage;
    private boolean outOfOrderEnabled;
    private int outOfOrderMaxDelayInSeconds;

    public AtomSimulationConfiguration() {
        super();
    }

    public AtomSimulationConfiguration(String pathToConfFile) throws UtilityLayerException {
        super(pathToConfFile);
    }

    @Override
    protected void setAttributesFromProperties() throws UtilityLayerException {
        LOGGER.debug("Setting up configuration attributes from properties");
        getAgentsAndOrderBooksParameters();
        getMarketMakerParameters();
        getNbTicksParameters();
        getPriceAndQuantityParameters();
        getTimeParameters();
        getOutputsParameters();
        LOGGER.debug("All configuration attributes have been set from properties");
        LOGGER.debug(this.toString());
    }


    private void getAgentsAndOrderBooksParameters() throws UtilityLayerException {
        LOGGER.debug("Setting up Agents and OrderBooks parameters");
        agentsParam = properties.getProperty("atom.agents", "");
        assert agentsParam != null;
        agentsRandom = Integer.parseInt(properties.getProperty(
                "atom.agents.random", "1000"));

        if ("random".equals(agentsParam)) {
            agents = new ArrayList<>(agentsRandom);
            for (int i = 0; i < agentsRandom; i++) {
                agents.add("Agent" + i);
            }
        } else {
            agents = Arrays
                    .asList(properties.getProperty(
                            "symbols.agents." + agentsParam, "").split(
                            "\\s*,\\s*"));
        }

        orderBooksParam = properties.getProperty("atom.orderbooks", "");
        assert orderBooksParam != null;
        orderBooksRandom = Integer.parseInt(properties.getProperty(
                "atom.orderbooks.random", "100"));

        if ("random".equals(orderBooksParam)) {
            orderBooks = new ArrayList<String>(orderBooksRandom);
            for (int i = 0; i < orderBooksRandom; i++) {
                orderBooks.add("Orderbook" + i);
            }
        } else {
            orderBooks = Arrays.asList(properties.getProperty(
                    "symbols.orderbooks." + orderBooksParam, "").split(
                    "\\s*,\\s*"));
        }

        if (agents.isEmpty() || orderBooks.isEmpty()) {
            LOGGER.error("Agents/Orderbooks not set");
            throw new UtilityLayerException("agents/orderbooks not set");
        }

        nbAgents = agents.size();
        nbOrderBooks = orderBooks.size();
    }

    private void getMarketMakerParameters() {
        LOGGER.debug("Setting up MarketMakers parameters");
        this.marketMarker = properties.getProperty("atom.marketmaker", "true").equals("true");
        this.marketMakerQuantity = Integer.parseInt(properties.getProperty("atom.marketmaker.quantity", "1"));
    }

    private void getNbTicksParameters() {
        LOGGER.debug("Setting up Ticks parameters");
        this.tickOpening = Integer.parseInt(properties.getProperty("simul.tick.opening", "0"));
        this.tickContinuous = Integer.parseInt(properties.getProperty("simul.tick.continuous", "10"));
        this.tickClosing = Integer.parseInt(properties.getProperty("simul.tick.closing", "0"));
    }

    private void getPriceAndQuantityParameters() {
        LOGGER.debug("Setting up Price and Quantity parameters");
        this.agentCash = Integer.parseInt(properties.getProperty("simul.agent.cash", "0"));
        this.agentMinPrice = Integer.parseInt(properties.getProperty("simul.agent.minprice", "10000"));
        this.agentMaxPrice = Integer.parseInt(properties.getProperty("simul.agent.maxprice", "20000"));
        this.agentMinQuantity = Integer.parseInt(properties.getProperty("simul.agent.minquantity", "10"));
        this.agentMaxQuantity = Integer.parseInt(properties.getProperty("simul.agent.maxquantity", "50"));
    }

    private void getTimeParameters() {
        LOGGER.debug("Setting up Time parameters");
        this.days = Integer.parseInt(properties.getProperty("simul.days", "1"));
        this.tsbTimeZone = properties.getProperty("simul.time.timezone");
        assert tsbTimeZone != null;
        this.tsbDateBegin = properties.getProperty("simul.time.startdate");
        assert tsbDateBegin != null;

        //take the hours
        this.tsbOpenHour = properties.getProperty("simul.time.openhour");
        this.tsbCloseHour = properties.getProperty("simul.time.closehour");


        // get the time representation parameters
        this.timestampEnabled = Boolean.parseBoolean(properties.getProperty("simul.time.timestamp.enabled", "false"));
        this.timestampHumanReadableEnabled = Boolean.parseBoolean(properties.getProperty("simul.time.timestamp.human.readable", "false"));
        this.dateFormat = properties.getProperty("simul.time.date.format", "yyyy-MM-dd");
        this.timeFormat = properties.getProperty("simul.time.time.format", "HH:mm:ss");

        // get parameters for out of order logs
        this.outOfOrderEnabled = Boolean.parseBoolean(properties.getProperty("simul.time.outOfOrder.enabled", "false"));
        this.outOfOrderCoefficient = Integer.parseInt(properties.getProperty("simul.time.outOfOrder.coefficient", "0"));
        this.outOfOrderPercentage = Integer.parseInt(properties.getProperty("simul.time.outOfOrder.percentage", "0"));
        this.outOfOrderMaxDelayInSeconds = Integer.parseInt(properties.getProperty("simul.time.outOfOrder.maxDelayInSeconds", "0"));



    }

    private void getOutputsParameters() {
        LOGGER.debug("Setting up output parameters");
        this.outKafka = Boolean.parseBoolean(properties.getProperty("simul.output.kafka", "false"));
        this.outFile = Boolean.parseBoolean(properties.getProperty("simul.output.file", "false"));
        this.pathToOutputFile = properties.getProperty("simul.output.file.path", "");
    }


    public List<String> getAgents() {
        return agents;
    }

    public List<String> getOrderBooks() {
        return orderBooks;
    }

    public int getTickOpening() {
        return tickOpening;
    }

    public int getDays() {
        return days;
    }

    public int getTickClosing() {
        return tickClosing;
    }

    public int getTickContinuous() {
        return tickContinuous;
    }

    public boolean isOutKafka() {
        return outKafka;
    }

    public String getAgentsParam() {
        return agentsParam;
    }

    public String getOrderBooksParam() {
        return orderBooksParam;
    }

    public int getOrderBooksRandom() {
        return orderBooksRandom;
    }

    public int getAgentsRandom() {
        return agentsRandom;
    }

    public boolean isMarketMarker() {
        return marketMarker;
    }

    public int getMarketMakerQuantity() {
        return marketMakerQuantity;
    }

    public int getAgentCash() {
        return agentCash;
    }

    public int getAgentMinPrice() {
        return agentMinPrice;
    }

    public int getAgentMaxPrice() {
        return agentMaxPrice;
    }

    public int getAgentMinQuantity() {
        return agentMinQuantity;
    }

    public int getAgentMaxQuantity() {
        return agentMaxQuantity;
    }

    public String getTsbDateBegin() {
        return tsbDateBegin;
    }

    public String getTsbOpenHour() {
        return tsbOpenHour;
    }

    public String getTsbCloseHour() {
        return tsbCloseHour;
    }

    public int getNbAgents() {
        return nbAgents;
    }

    public int getNbOrderBooks() {
        return nbOrderBooks;
    }

    public String getTsbTimeZone() {
        return tsbTimeZone;
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

    public void setAgentsParam(String agentsParam) {
        this.agentsParam = agentsParam;
    }

    public void setAgents(List<String> agents) {
        this.agents = agents;
    }

    public void setOrderBooksParam(String orderBooksParam) {
        this.orderBooksParam = orderBooksParam;
    }

    public void setOrderBooks(List<String> orderBooks) {
        this.orderBooks = orderBooks;
    }

    public void setOrderBooksRandom(int orderBooksRandom) {
        this.orderBooksRandom = orderBooksRandom;
    }

    public void setAgentsRandom(int agentsRandom) {
        this.agentsRandom = agentsRandom;
    }

    public void setTickOpening(int tickOpening) {
        this.tickOpening = tickOpening;
    }

    public void setTickContinuous(int tickContinuous) {
        this.tickContinuous = tickContinuous;
    }

    public void setTickClosing(int tickClosing) {
        this.tickClosing = tickClosing;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public void setMarketMarker(boolean marketMarker) {
        this.marketMarker = marketMarker;
    }

    public void setMarketMakerQuantity(int marketMakerQuantity) {
        this.marketMakerQuantity = marketMakerQuantity;
    }

    public void setOutKafka(boolean outKafka) {
        this.outKafka = outKafka;
    }

    public void setAgentCash(int agentCash) {
        this.agentCash = agentCash;
    }

    public void setAgentMinPrice(int agentMinPrice) {
        this.agentMinPrice = agentMinPrice;
    }

    public void setAgentMaxPrice(int agentMaxPrice) {
        this.agentMaxPrice = agentMaxPrice;
    }

    public void setAgentMinQuantity(int agentMinQuantity) {
        this.agentMinQuantity = agentMinQuantity;
    }

    public void setAgentMaxQuantity(int agentMaxQuantity) {
        this.agentMaxQuantity = agentMaxQuantity;
    }

    public void setTsbTimeZone(String tsbTimeZone) {
        this.tsbTimeZone = tsbTimeZone;
    }

    public void setTsbDateBegin(String tsbDateBegin) {
        this.tsbDateBegin = tsbDateBegin;
    }

    public void setTsbOpenHour(String tsbOpenHour) {
        this.tsbOpenHour = tsbOpenHour;
    }

    public void setTsbCloseHour(String tsbCloseHour) {
        this.tsbCloseHour = tsbCloseHour;
    }

    public void setNbAgents(int nbAgents) {
        this.nbAgents = nbAgents;
    }

    public void setNbOrderBooks(int nbOrderBooks) {
        this.nbOrderBooks = nbOrderBooks;
    }


    public boolean isTimestampEnabled() {
        return timestampEnabled;
    }

    public void setTimestampEnabled(boolean timestampEnabled) {
        this.timestampEnabled = timestampEnabled;
    }

    public boolean isTimestampHumanReadableEnabled() {
        return timestampHumanReadableEnabled;
    }

    public void setTimestampHumanReadableEnabled(boolean timestampHumanReadableEnabled) {
        this.timestampHumanReadableEnabled = timestampHumanReadableEnabled;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public int getOutOfOrderCoefficient() {
        return outOfOrderCoefficient;
    }

    public void setOutOfOrderCoefficient(int outOfOrderCoefficient) {
        this.outOfOrderCoefficient = outOfOrderCoefficient;
    }

    public int getOutOfOrderPercentage() {
        return outOfOrderPercentage;
    }

    public void setOutOfOrderPercentage(int outOfOrderPercentage) {
        this.outOfOrderPercentage = outOfOrderPercentage;
    }

    public boolean isOutOfOrderEnabled() {
        return outOfOrderEnabled;
    }

    public void setOutOfOrderEnabled(boolean outOfOrderEnabled) {
        this.outOfOrderEnabled = outOfOrderEnabled;
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
        return "AtomSimulationConfiguration {" + "\n" +
                "   - agentsParam='" + agentsParam + '\'' + "\n" +
                "   - agents=" + agents + "\n" +
                "   - orderBooksParam='" + orderBooksParam + '\'' + "\n" +
                "   - orderBooks=" + orderBooks + "\n" +
                "   - orderBooksRandom=" + orderBooksRandom + "\n" +
                "   - agentsRandom=" + agentsRandom + "\n" +
                "   - tickOpening=" + tickOpening + "\n" +
                "   - tickContinuous=" + tickContinuous + "\n" +
                "   - tickClosing=" + tickClosing + "\n" +
                "   - days=" + days + "\n" +
                "   - marketMarker=" + marketMarker + "\n" +
                "   - marketMakerQuantity=" + marketMakerQuantity + "\n" +
                "   - outKafka=" + outKafka + "\n" +
                "   - outFile=" + outFile + "\n" +
                "   - pathToOutputFile='" + pathToOutputFile + '\'' + "\n" +
                "   - agentCash=" + agentCash + "\n" +
                "   - agentMinPrice=" + agentMinPrice + "\n" +
                "   - agentMaxPrice=" + agentMaxPrice + "\n" +
                "   - agentMinQuantity=" + agentMinQuantity + "\n" +
                "   - agentMaxQuantity=" + agentMaxQuantity + "\n" +
                "   - tsbTimeZone='" + tsbTimeZone + '\'' + "\n" +
                "   - tsbDateBegin='" + tsbDateBegin + '\'' + "\n" +
                "   - tsbOpenHour='" + tsbOpenHour + '\'' + "\n" +
                "   - tsbCloseHour='" + tsbCloseHour + '\'' + "\n" +
                "   - nbAgents=" + nbAgents + "\n" +
                "   - nbOrderBooks=" + nbOrderBooks + "\n" +
                "   - timestampEnabled=" + timestampEnabled + "\n" +
                "   - timestampHumanReadableEnabled=" + timestampHumanReadableEnabled + "\n" +
                "   - dateFormat='" + dateFormat + '\'' + "\n" +
                "   - timeFormat='" + timeFormat + '\'' + "\n" +
                "   - outOfOrderCoefficient=" + outOfOrderCoefficient + "\n" +
                "   - outOfOrderPercentage=" + outOfOrderPercentage + "\n" +
                "   - outOfOrderEnabled=" + outOfOrderEnabled + "\n" +
                "   - outOfOrderMaxDelayInSeconds=" + outOfOrderMaxDelayInSeconds + "\n" +
                '}';
    }
}
