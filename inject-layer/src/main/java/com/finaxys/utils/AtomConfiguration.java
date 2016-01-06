package com.finaxys.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main configuration object that holds all properties.
 */
public class AtomConfiguration {

    private static final Logger LOGGER = Logger
            .getLogger(AtomConfiguration.class.getName());

    // Business Data
    private String agentsParam;
    private List<String> agents = new ArrayList<String>();
    private String orderBooksParam;
    private List<String> orderBooks = new ArrayList<String>();

    private int orderBooksRandom;
    private int agentsRandom;

    private int tickOpening;
    private int tickContinuous;
    private int tickClosing;
    private int days;

    private boolean marketMarker;
    private int marketMakerQuantity;


    // App data
    private boolean outKafka;

    private String kafkaTopic;
    private String kafkaBoot;

    private int agentCash;
    private int agentMinPrice;
    private int agentMaxPrice;
    private int agentMinQuantity;
    private int agentMaxQuantity;

    // TimeStampBuilder
    private String tsbDateBegin;
    private String tsbOpenHour;
    private String tsbCloseHour;
    private int tsbNbTickMax;
    private int nbAgents;
    private int nbOrderBooks;

    private static AtomConfiguration instance;

    private AtomConfiguration() throws InjectLayerException {
        load();
    }

    public static final AtomConfiguration getInstance() {
        if (instance == null) {
            synchronized (AtomConfiguration.class) {
                if (instance == null) {
                    instance = new AtomConfiguration();
                    instance.load();
                }
            }
        }
        return instance;
    }

    private void load() throws InjectLayerException {

        // loading properties.txt into System Properties.
        Properties p = new Properties(System.getProperties());

        try {
            p.load(this.getClass().getClassLoader().getResourceAsStream("config/config.properties"));
        } catch (IOException e) {
            LOGGER.info("Not able to load properties from file");
            throw new InjectLayerException(e.getMessage());
        }
        System.setProperties(p);
        // Loading properties in local variables
        try {

            // Get agents & orderbooks
            agentsParam = System.getProperty("atom.agents", "");
            assert agentsParam != null;
            LOGGER.info("agsym = " + agentsParam);
            agentsRandom = Integer.parseInt(System.getProperty(
                    "atom.agents.random", "1000"));

            if ("random".equals(agentsParam)) {
                agents = new ArrayList<String>(agentsRandom);
                for (int i = 0; i < agentsRandom; i++) {
                    agents.add("Agent" + i);
                }
            } else {
                agents = Arrays
                        .asList(System.getProperty(
                                "symbols.agents." + agentsParam, "").split(
                                "\\s*,\\s*"));
            }

            orderBooksParam = System.getProperty("atom.orderbooks", "");
            assert orderBooksParam != null;
            LOGGER.info("obsym = " + orderBooksParam);
            orderBooksRandom = Integer.parseInt(System.getProperty(
                    "atom.orderbooks.random", "100"));

            if ("random".equals(orderBooksParam)) {
                orderBooks = new ArrayList<String>(orderBooksRandom);
                for (int i = 0; i < orderBooksRandom; i++) {
                    orderBooks.add("Orderbook" + i);
                }
            } else {
                orderBooks = Arrays.asList(System.getProperty(
                        "symbols.orderbooks." + orderBooksParam, "").split(
                        "\\s*,\\s*"));
            }

            if (agents.isEmpty() || orderBooks.isEmpty()) {
                LOGGER.log(Level.SEVERE, "Agents/Orderbooks not set");
                throw new IOException("agents/orderbooks not set");
            }

            nbAgents = agents.size();

            nbOrderBooks = orderBooks.size();



            this.outKafka = Boolean.parseBoolean(System.getProperty(
                    "simul.output.kafka", "false"));
            this.marketMarker = System.getProperty("atom.marketmaker", "true")
                    .equals("true");
            this.marketMakerQuantity = Integer.parseInt(System.getProperty("atom.marketmaker.quantity", "1"));




            this.tickOpening = Integer.parseInt(System.getProperty(
                    "simul.tick.opening", "0"));
            this.tickContinuous = Integer.parseInt(System.getProperty(
                    "simul.tick.continuous", "10"));
            this.tickClosing = Integer.parseInt(System.getProperty(
                    "simul.tick.closing", "0"));
            this.days = Integer.parseInt(System.getProperty("simul.days", "1"));


            this.agentCash = Integer.parseInt(System.getProperty("simul.agent.cash", "0"));
            this.agentMinPrice = Integer.parseInt(System.getProperty("simul.agent.minprice", "10000"));
            this.agentMaxPrice = Integer.parseInt(System.getProperty("simul.agent.maxprice", "20000"));
            this.agentMinQuantity = Integer.parseInt(System.getProperty("simul.agent.minquantity", "10"));
            this.agentMaxQuantity = Integer.parseInt(System.getProperty("simul.agent.maxquantity", "50"));


            this.kafkaTopic = System.getProperty("kafka.topic");
            this.kafkaBoot = System.getProperty("bootstrap.kafka.servers");

            this.tsbDateBegin = System.getProperty("simul.time.startdate");
            assert tsbDateBegin != null;

            //take the hours
            this.tsbOpenHour = System.getProperty("simul.time.openhour");
            this.tsbCloseHour = System.getProperty("simul.time.closehour");

            //Take the period
            String nbTickMaxStr = System.getProperty("simul.tick.continuous");
            //LOGGER.info("simul.tick.continuous = " + nbTickMaxStr);
            this.tsbNbTickMax = Integer.parseInt(nbTickMaxStr);
            assert nbTickMaxStr != null;

        } catch (IOException e) {
            throw new InjectLayerException("cannot load Atom Configuration",
                    e);
        }

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

    public int getTsbNbTickMax() {
        return tsbNbTickMax;
    }

    public int getNbAgents() {
        return nbAgents;
    }

    public int getNbOrderBooks() {
        return nbOrderBooks;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public String getKafkaBoot() {
        return kafkaBoot;
    }

}
