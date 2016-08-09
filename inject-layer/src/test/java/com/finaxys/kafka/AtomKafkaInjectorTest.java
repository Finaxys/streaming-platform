package com.finaxys.kafka;

import com.finaxys.utils.AtomInjectConfiguration;
import info.batey.kafka.unit.KafkaUnit;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNull.*;
import static org.junit.Assert.fail;

import org.junit.*;
import v13.*;
import v13.agents.Agent;
import v13.agents.DumbAgent;


public class AtomKafkaInjectorTest {

    // Kafka server for unit tests
    private static KafkaUnit kafkaUnit;

    // class under test
    private static AtomKafkaInjector atomKafkaInjector;

    // class used by the class under test
    private static AtomInjectConfiguration atomInjectConfiguration;

    // basic data to test AtomKafkaInjector methods
    private static String topicName;
    private static long timestamp = 12344321;
    private static OrderBook orderBookForTest;
    private static Agent agentForTest;
    private static Order askOrderForTest;
    private static PriceRecord priceRecordNull = null;
    private static PriceRecord priceRecordNotNull;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // instanciate an AtomInjectConfiguration using test/ressources/config/config.properties
        atomInjectConfiguration = AtomInjectConfiguration.getInstance();

        // instanciate the class under test
        atomKafkaInjector = new AtomKafkaInjector(atomInjectConfiguration);

        // set the topic name according to the config.properties file
        topicName = atomInjectConfiguration.getKafkaTopic();

        // instanciate kafka server, start it and create topic
        kafkaUnit = new KafkaUnit("localhost:9091", atomInjectConfiguration.getKafkaBoot());
        kafkaUnit.startup();
        kafkaUnit.createTopic(topicName);

        // ========== Create a simulation ==========
        final String APPLE = "AAPL";
        MarketPlace market = new MarketPlace();
        // Orderbook creation to represent the Apple asset
        orderBookForTest = new OrderBook(APPLE);
        market.orderBooks.put(APPLE, orderBookForTest);
        // We need an agentForTest to keep track of assets evolution
        DumbAgent agent = new DumbAgent("dumb");
        // First, we want to sell 1000 Apple assets at 350.00 euros
        market.send(agent, new LimitOrder(APPLE, "order1", LimitOrder.ASK, 1000, (long) 35000));
        // No matching askOrderForTest yet, so the askOrderForTest wait within the orderbook
        // A new askOrderForTest is created to buy 500 Apple assets at the current market price
        market.send(agent, new MarketOrder(APPLE, "order2", LimitOrder.BID, 500));

        // Use the simulation to set attributes used to test the class under test methods
        agentForTest = agent;
        askOrderForTest = market.orderBooks.get(APPLE).ask.first();
        priceRecordNotNull = orderBookForTest.lastFixedPrice;
        assertThat(priceRecordNotNull, notNullValue());
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        // shutdown the kafka server
        kafkaUnit.shutdown();
    }

    // Simple test to check Producer and Consumer in a Kafka topic
    @Test
    public void producerAndConsumerTest() throws Exception {

        // set number of messages sent in the test
        int numberOfMessagesSentToKafka = 1;

        // create Producer
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUnit.getKafkaConnect());
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
        Producer<String, String> producer = new KafkaProducer<>(producerProperties);

        // create and send message
        // One of the four ProducerRecord constructors : ProducerRecord(String topic, V value)
        ProducerRecord<String, String> data = new ProducerRecord<>(topicName, "a message");
        producer.send(data);

        // read message from the topic with KafkaUnit.readMessages(...) method
        List<String> messagesList = null;
        try {
            messagesList = kafkaUnit.readMessages(topicName, numberOfMessagesSentToKafka);
        }
        catch (TimeoutException e) {
            fail("There should be " + numberOfMessagesSentToKafka + " retourned messages : " + e);
        }

        // assertions
        assertThat(messagesList.get(0), is("a message"));
        assertThat(messagesList.size(), is(numberOfMessagesSentToKafka));
    }


    @Test
    public void sendAgentTest() throws Exception {

        /* method under test is
         * public void sendAgent(long ts, Agent a, Order o, PriceRecord pr) */

        // local test data
        int numberOfMessagesSentToKafka = 2;

        // Build records just as sendAgent() would build them
        String messageWithPriceRecordNotNull = new StringBuilder()
                .append("Agent").append(";")
                .append(agentForTest.name).append(";")
                .append(agentForTest.cash).append(";")
                .append(askOrderForTest.obName).append(";")
                .append(agentForTest.getInvest(askOrderForTest.obName)).append(";")
                .append((priceRecordNotNull != null ? Long.valueOf(priceRecordNotNull.price) : "none")).append(";")
                .append(timestamp)
                .toString();
        // --------------------------------------------   //
        String messageWithPriceRecordNull = new StringBuilder()
                .append("Agent").append(";")
                .append(agentForTest.name).append(";")
                .append(agentForTest.cash).append(";")
                .append(askOrderForTest.obName).append(";")
                .append(agentForTest.getInvest(askOrderForTest.obName)).append(";")
                .append((priceRecordNull != null ? Long.valueOf(priceRecordNull.price) : "none")).append(";")
                .append(timestamp)
                .toString();

        // call the method under test twice :
        //   - first with a PriceRecord not null
        //   - then with a PriceRecord set to null
        atomKafkaInjector.sendAgent(timestamp, agentForTest, askOrderForTest, priceRecordNotNull);
        atomKafkaInjector.sendAgent(timestamp, agentForTest, askOrderForTest, priceRecordNull);

        // read message from the topic with KafkaUnit.readMessages(...) method
        List<String> messagesList = null;
        try {
            messagesList = kafkaUnit.readMessages(topicName, numberOfMessagesSentToKafka);
        }
        catch (TimeoutException e) {
            fail("There should be " + numberOfMessagesSentToKafka + " retourned messages : " + e);
        }

        // assertions
        assertThat(messagesList.size(), is(numberOfMessagesSentToKafka));
        assertThat(messagesList.get(0), is(messageWithPriceRecordNotNull));
        assertThat(messagesList.get(1), is(messageWithPriceRecordNull));
    }

    @Test
    public void sendPriceRecordTest() throws Exception {

        /* method under test is
         * public void sendPriceRecord(long ts, PriceRecord pr, long bestAskPrice, long bestBidPrice) */

        // local test data
        int numberOfMessagesSentToKafka = 1;

        // Build records just as sendPriceRecord() would build them
        long bestAskPrice = 100;
        long bestBidPrice = 100;
        String messageToTest = new StringBuilder()
                .append("Price").append(";")
                .append(priceRecordNotNull).append(";")
                .append(bestAskPrice).append(";")
                .append(bestBidPrice).append(";")
                .append(timestamp)
                .toString();

        // call to the method under test
        atomKafkaInjector.sendPriceRecord(timestamp, priceRecordNotNull, bestAskPrice, bestBidPrice);

        // read message from the topic with KafkaUnit.readMessages(...) method
        List<String> messagesList = null;
        try {
            messagesList = kafkaUnit.readMessages(topicName, numberOfMessagesSentToKafka);
        }
        catch (TimeoutException e) {
            fail("There should be " + numberOfMessagesSentToKafka + " retourned messages : " + e);
        }

        // assertions
        assertThat(messagesList.size(), is(numberOfMessagesSentToKafka));
        assertThat(messagesList.get(0), is(messageToTest));
    }

    @Test
    public void sendOrderTest() throws Exception {

        /* method under test is
         * public void sendOrder(long ts, Order o) */

        // local test data
        int numberOfMessagesSentToKafka = 1;

        // Build records just as sendPriceRecord() would build them
        String messageToTest = new StringBuilder()
                .append(askOrderForTest.toString()).append(";")
                .append(timestamp)
                .toString();

        // call to the method under test
        atomKafkaInjector.sendOrder(timestamp, askOrderForTest);

        // read message from the topic with KafkaUnit.readMessages(...) method
        List<String> messagesList = null;
        try {
            messagesList = kafkaUnit.readMessages(topicName, numberOfMessagesSentToKafka);
        }
        catch (TimeoutException e) {
            fail("There should be " + numberOfMessagesSentToKafka + " retourned messages : " + e);
        }

        // assertions
        assertThat(messagesList.size(), is(numberOfMessagesSentToKafka));
        assertThat(messagesList.get(0), is(messageToTest));
    }

    @Test
    public void sendTickTest() throws Exception {

        /* method under test is
         * public void sendTick(long ts, Day day, Collection<OrderBook> orderbooks) */

        // local test data
        List<OrderBook> orderBookList = new ArrayList<>();
        orderBookList.add(orderBookForTest);
        Day day = Day.createEuroNEXT(1, 100, 1);
        /* In a instance of Day, currentPeriod is an iterator for a Period[] attribute.
         * When creating a Day, currentPeriod is set by default to -1.
         * In order to access currentPeriod and currentTick, we need to increment it by one,
         *   and we call Day.nextPeriod() method to do that. */
        day.nextPeriod();
        List<String> messagesToTestList = new ArrayList<>();
        int numberOfMessagesSentToKafka = orderBookList.size();





        // Build records just as sendPriceRecord() would build them
        for (OrderBook ob : orderBookList) {
            StringBuilder sb = new StringBuilder()
                    .append("Tick").append(";")
                    .append(day.currentPeriod().currentTick()).append(";")
                    .append(ob.obName).append(";")
                    .append(ob.ask.size() > 0 ? Long.valueOf(((LimitOrder) ob.ask.first()).price) : "0").append(";")
                    .append(ob.bid.size() > 0 ? Long.valueOf(((LimitOrder) ob.bid.first()).price) : "0").append(";")
                    .append(ob.lastFixedPrice != null ? Long.valueOf(ob.lastFixedPrice.price) : "0").append(";")
                    .append(timestamp);
            messagesToTestList.add(sb.toString());
        }

        // call to the method under test
        atomKafkaInjector.sendTick(timestamp, day, orderBookList);

        // read message from the topic with KafkaUnit.readMessages(...) method
        List<String> messagesList = null;
        try {
            messagesList = kafkaUnit.readMessages(topicName, numberOfMessagesSentToKafka);
        }
        catch (TimeoutException e) {
            fail("There should be " + numberOfMessagesSentToKafka + " retourned messages : " + e);
        }

        // assertions
        assertThat(messagesList.size(), is(numberOfMessagesSentToKafka));
        for (int i=0; i<messagesToTestList.size(); i++) {
            assertThat(messagesToTestList.get(i), is(messagesList.get(i)));
        }
    }

    @Test
    public void sendDayTest() throws Exception {

        /* method under test is
         * public void sendDay(long ts, int nbDays, Collection<OrderBook> orderbooks) */

        // local test data
        List<OrderBook> orderBookList = Arrays.asList(orderBookForTest);
        List<String> messagesToTestList = new ArrayList<>();
        int numberOfMessagesSentToKafka = orderBookList.size();
        int nbDays = 2;


        // Build records just as sendDay() would build them
        for (OrderBook ob : orderBookList) {
            StringBuilder sb = new StringBuilder()
                    .append("Day").append(";")
                    .append(nbDays).append(";")
                    .append(ob.obName).append(";")
                    .append(ob.firstPriceOfDay).append(";")
                    .append(ob.lowestPriceOfDay).append(";")
                    .append(ob.highestPriceOfDay).append(";")
                    .append(ob.lastPriceOfDay).append(";")
                    .append(ob.numberOfPricesFixed).append(";")
                    .append(timestamp);
            messagesToTestList.add(sb.toString());
        }

        // call to the method under test
        atomKafkaInjector.sendDay(timestamp, nbDays, orderBookList);

        // read message from the topic with KafkaUnit.readMessages(...) method
        List<String> messagesList = null;
        try {
            messagesList = kafkaUnit.readMessages(topicName, numberOfMessagesSentToKafka);
        }
        catch (TimeoutException e) {
            fail("There should be " + numberOfMessagesSentToKafka + " retourned messages : " + e);
        }

        // assertions
        assertThat(messagesList.size(), is(numberOfMessagesSentToKafka));
        for (int i=0; i<messagesToTestList.size(); i++) {
            assertThat(messagesToTestList.get(i), is(messagesList.get(i)));
        }
    }

    @Test
    public void sendExecTest() throws Exception {

        /* method under test is
         * public void sendExec(long ts, Order o) */

        // local test data
        int numberOfMessagesSentToKafka = 1;

        // Build records just as sendPriceRecord() would build them
        String messageToTest = new StringBuilder()
                .append("Exec").append(";")
                .append(askOrderForTest.sender.name).append(";")
                .append(askOrderForTest.extId).append(";")
                .append(timestamp)
                .toString();

        // call to the method under test
        atomKafkaInjector.sendExec(timestamp, askOrderForTest);

        // read message from the topic with KafkaUnit.readMessages(...) method
        List<String> messagesList = null;
        try {
            messagesList = kafkaUnit.readMessages(topicName, numberOfMessagesSentToKafka);
        }
        catch (TimeoutException e) {
            fail("There should be " + numberOfMessagesSentToKafka + " retourned messages : " + e);
        }

        // assertions
        assertThat(messagesList.size(), is(numberOfMessagesSentToKafka));
        assertThat(messagesList.get(0), is(messageToTest));
    }
}


