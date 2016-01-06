package com.finaxys.utils;

import java.io.FileInputStream;
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

    private int dayGap;
    private int tickOpening;
    private int tickContinuous;
    private int tickClosing;
    private int days;

    private boolean marketMarker;
    private int marketMakerQuantity;

    // HBase - MultiThreading - buff options
    private int worker;
    private int flushRatio;
    private int bufferSize;
    private int stackPuts;
    private boolean autoFlush;
    private byte[] cfName;
    private String tableName = "trace";
    private String sparkTableName;

    // App data
    private long startTime;
    private boolean outHbase;
    private boolean outSystem;
    private String outFilePath;
    // private boolean replay;
    // private String replaySource;
    private boolean outFile;
    private boolean outKafka;
    private boolean outAvro;
    private String avroSchema;
    private String avroHDFSDest;
    private String parquetHDFSDest;
    private String pathAvro;

    private String hadoopConfCore;
    private String hbaseConfHbase;
    private String hadoopConfHdfs;
    private String kafkaTopic;
    private String kafkaBoot;
    private String kafkaQuorum;

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
            p.load(new FileInputStream("config/config.properties"));
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


            this.outFile = Boolean.parseBoolean(System.getProperty(
                    "simul.output.file", "false"));
            this.outKafka = Boolean.parseBoolean(System.getProperty(
                    "simul.output.kafka", "false"));
            this.outFilePath = System.getProperty("simul.output.file.path",
                    "outPutAtom.log");
            this.outSystem = System.getProperty("simul.output.standard",
                    "false").equals("false");
            this.dayGap = Integer.parseInt(System.getProperty(
                    "simul.day.startDay", "1")) - 1;

            this.marketMarker = System.getProperty("atom.marketmaker", "true")
                    .equals("true");
            this.marketMakerQuantity = Integer.parseInt(System.getProperty("atom.marketmaker.quantity", "1"));

            this.outAvro = System.getProperty("simul.output.avro", "true")
                    .equals("true");
            this.avroSchema = System.getProperty("avro.schema");
            this.parquetHDFSDest = System.getProperty("dest.hdfs.parquet");
            this.startTime = System.currentTimeMillis();
            this.worker = Integer.parseInt(System.getProperty("simul.worker",
                    "10"));
            this.flushRatio = Integer.parseInt(System.getProperty(
                    "simul.flushRatio", "1000"));
            this.bufferSize = Integer.parseInt(System.getProperty(
                    "simul.bufferSize", "10000"));


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


            this.hadoopConfCore = System.getProperty("hadoop.conf.core");
            this.hadoopConfHdfs = System.getProperty("hadoop.conf.hdfs");

            this.avroHDFSDest = System.getProperty("dest.hdfs.avro");
            this.pathAvro = System.getProperty("avro.path");
            this.kafkaTopic = System.getProperty("kafka.topic");
            this.kafkaQuorum = System.getProperty("kafka.quorum");
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

    public String getTableName() {
        return tableName;
    }

    public byte[] getColumnFamily() {
        return cfName;
    }

    public boolean isAutoFlush() {
        return autoFlush;
    }

    public List<String> getAgents() {
        return agents;
    }

    public List<String> getOrderBooks() {
        return orderBooks;
    }

    public int getDayGap() {
        return dayGap;
    }

    public int getWorker() {
        return worker;
    }

    public int getFlushRatio() {
        return flushRatio;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public int getStackPuts() {
        return stackPuts;
    }

    public byte[] getCfName() {
        return cfName;
    }

    public long getStartTime() {
        return startTime;
    }

    public boolean isOutHbase() {
        return outHbase;
    }

    public boolean isOutSystem() {
        return outSystem;
    }

    public String getOutFilePath() {
        return outFilePath;
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


    public boolean isOutFile() {
        return outFile;
    }

    public boolean isOutKafka() {
        return outKafka;
    }

    public boolean isOutAvro() {
        return outAvro;
    }

    public String getAvroSchema() {
        return avroSchema;
    }

    public String getAvroHDFSDest() {
        return avroHDFSDest;
    }

    public String getPathAvro() {
        return pathAvro;
    }

    public String getHadoopConfCore() {
        return hadoopConfCore;
    }

    public String getHbaseConfHbase() {
        return hbaseConfHbase;
    }

    public String getHadoopConfHdfs() {
        return hadoopConfHdfs;
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

    public String getParquetHDFSDest() {
        return parquetHDFSDest;
    }

    public void setParquetHDFSDest(String parquetHDFSDest) {
        this.parquetHDFSDest = parquetHDFSDest;
    }

    public String getSparkTableName() {
        return sparkTableName;
    }

    public void setSparkTableName(String sparkTableName) {
        this.sparkTableName = sparkTableName;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

    public String getKafkaBoot() {
        return kafkaBoot;
    }

    public void setKafkaBoot(String kafkaBoot) {
        this.kafkaBoot = kafkaBoot;
    }

    public String getKafkaQuorum() {
        return kafkaQuorum;
    }

    public void setKafkaQuorum(String kafkaQuorum) {
        this.kafkaQuorum = kafkaQuorum;
    }
}
