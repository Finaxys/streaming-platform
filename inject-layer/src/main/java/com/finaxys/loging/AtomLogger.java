package com.finaxys.loging;

import com.finaxys.loging.injectors.AtomDataInjector;
import configuration.AtomSimulationConfiguration;
import com.finaxys.utils.AtomTimeStampBuilder;
import model.atomlogs.AtomLog;
import model.atomlogs.AtomLogFactory;
import v13.*;
import v13.agents.Agent;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Class in charge of logging all messages produced by the ATOM simulation.
 *
 * Depending on the simulation parameters, this logger also add timestamps
 * to the ATOM original log :
 *      - if "simul.time.timestamp.enabled" is set to true, add a long timestamp
 *      to the original log
 *      - if "simul.time.timestamp.human.readable" is set to true, add a date time
 *      timestamp to the original log
 *
 * This logger also provide multiple output choices. By using an list of AtomDataInjector,
 * you can define one or several outputs (file, Kafka, database, ...)
 */
public class AtomLogger extends v13.Logger {

    private static Logger LOGGER = LogManager.getLogger(AtomLogger.class);

    private List<AtomDataInjector> injectors;
    protected AtomTimeStampBuilder tsb;
    private AtomBasicLogBuilder atomBasicLogBuilder;
    private AtomSimulationConfiguration conf;
    protected boolean isEndOfDay = false;


    /**
     * AtomLogger constructor.
     *
     * @param conf The AtomSimulationConfiguration parameters for the simulation and the injectors
     * @param injectors one or multiple injectors
     */
    public AtomLogger(AtomSimulationConfiguration conf, AtomDataInjector... injectors) {
        this.conf = conf;
        this.injectors = Arrays.asList(injectors);
        this.atomBasicLogBuilder = new AtomBasicLogBuilder();
        initTimeStampBuilder();
        openInjectors();
    }


    /**
     * Init the AtomTimeStampBuilder and set the time to pre-opening of the current day.
     */
    private void initTimeStampBuilder() {
        LOGGER.debug("Initializing TimeStampBuilder");
        tsb = new AtomTimeStampBuilder(conf);
        tsb.setTimestampForPreOpening();
        LOGGER.debug(tsb.toString());
        LOGGER.debug("TimeStampBuilder initialized");
    }


    /**
     * Open all injectors.
     */
    private void openInjectors() {
        LOGGER.debug("Opening all injectors");
        for (AtomDataInjector injector : injectors) {
            injector.createOutput();
        }
        LOGGER.debug("All injectors opened");
    }


    /**
     * Close all injectors.
     */
    public void closeInjectors() {
        LOGGER.debug("Closing all injectors");
        for (AtomDataInjector injector : injectors) {
            injector.closeOutput();
        }
        LOGGER.debug("All injectors closed");
    }


    /**
     * Append the event time at the beginning of the raw atom log
     *
     * @param logWithoutEventTime the log without the timestamp
     * @return the atom log with the timestamp inside
     */
    private String addEventTimeToLog(long eventTime, String dateTime, String logWithoutEventTime) {
        StringBuilder sb = new StringBuilder();

        if (conf.isTimestampEnabled())
            sb.append(eventTime).append(";");

        if (conf.isTimestampHumanReadableEnabled())
            sb.append(dateTime).append(";");

        sb.append(logWithoutEventTime);
        return sb.toString();
    }

    /**
     * Append the processing time at the beginning of the atom log
     *
     * @param logWithoutProcessingTime the log without the timestamp
     * @return the atom log with the processing time timestamp
     */
    private String addProcessingTimeToLog(long processingTime, String logWithoutProcessingTime) {
        if (conf.isTimestampEnabled())
            return new StringBuilder()
                    .append(processingTime).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                    .append(logWithoutProcessingTime)
                    .toString();
        else
            return logWithoutProcessingTime;

    }

    /**
     * Log an Order.
     * An order log occurs when an agent send an order to buy or sell a given symbol.
     *
     * @param o the Order to log
     */
    @Override
    public void order(Order o) {
        tsb.computeTimestampForCurrentTick();
        long timestampForCurrentTick = tsb.getTimestampForCurrentTick();
        String dateTime = tsb.getDateTimeForCurrentTick();
        String log = addEventTimeToLog(timestampForCurrentTick, dateTime, atomBasicLogBuilder.order(o));
        sendLog(log, timestampForCurrentTick);
    }


    /**
     * Log an Exec.
     * An exec log occurs when an order is issued and match with another order.
     * Then, if the new and/or the pending order are fully executed (all quantity
     * sold and/or bought), an exec log is sent for each fully executed order.
     *
     * @param o The Order responsible of the exec
     */
    @Override
    public void exec(Order o) {
        tsb.computeTimestampForCurrentTick();
        long timestampForCurrentTick = tsb.getTimestampForCurrentTick();
        String dateTime = tsb.getDateTimeForCurrentTick();
        String log = addEventTimeToLog(timestampForCurrentTick, dateTime, atomBasicLogBuilder.exec(o));
        sendLog(log, timestampForCurrentTick);
    }

    /**
     * Log an Agent.
     * An agent log occurs when an order is issued and match with another order.
     * Two agent logs are then sent : one for each agent (buyer and seller).
     *
     * @param a The Agent
     * @param o The Order
     * @param pr The PriceRecord generated
     */
    @Override
    public void agent(Agent a, Order o, PriceRecord pr) {
        tsb.computeTimestampForCurrentTick();
        long timestampForCurrentTick = tsb.getTimestampForCurrentTick();
        String dateTime = tsb.getDateTimeForCurrentTick();
        String log = addEventTimeToLog(timestampForCurrentTick, dateTime, atomBasicLogBuilder.agent(a, o, pr));
        sendLog(log, timestampForCurrentTick);
    }


    /**
     * Log a Price.
     * A price log occurs when an order is issued and match with another order.
     * Prices are logged during all periods (opening, intraday and closing).
     * When we are in the opening or closing period, the computeTimestampForCurrentTick()
     * method will return a timestamp corresponding to the beginning or the end of the day.
     * Otherwise, il will calculate the timestamp for the current intraday tick.
     *
     * @param pr The PriceRecord
     * @param bestAskPrice The best ask price
     * @param bestBidPrice The best bid price
     */
    @Override
    public void price(PriceRecord pr, long bestAskPrice, long bestBidPrice) {
        tsb.computeTimestampForCurrentTick();
        long timestampForCurrentTick = tsb.getTimestampForCurrentTick();
        String dateTime = tsb.getDateTimeForCurrentTick();
        String log = addEventTimeToLog(timestampForCurrentTick, dateTime, atomBasicLogBuilder.price(pr, bestAskPrice, bestBidPrice));
        sendLog(log, timestampForCurrentTick);
    }

    /**
     * Log a Tick.
     * One log for each OrderBook in the simulation.
     * There is generally one tick on opening and closing period and multiple ones in intraday.
     * But there can be multiple ticks in opening/closing period as well.
     *
     * @param day The Day in which the tick happened
     * @param orderbooks The list of OrderBooks of the simulation
     */
    @Override
    public void tick(Day day, java.util.Collection<OrderBook> orderbooks) {
        List<String> logList = new ArrayList<>();
        tsb.computeTimestampForCurrentTick();
        long timestampForCurrentTick = tsb.getTimestampForCurrentTick();
        String dateTime = tsb.getDateTimeForCurrentTick();

        for (OrderBook ob : orderbooks)
            logList.add(addEventTimeToLog(timestampForCurrentTick, dateTime, atomBasicLogBuilder.tick(day, ob)));

        for (String log : logList)
            sendLog(log, timestampForCurrentTick);

        logSimulationProgression();

        tsb.incrementCurrentTick();
        tsb.computeTimestampForCurrentTick();
    }

    /**
     * Log a Day.
     * One log for each OrderBook in the simulation.
     *
     * @param numOfDay The number of the day to log
     * @param orderbooks The list of OrderBooks of the simulation
     */
    @Override
    public void day(int numOfDay, java.util.Collection<OrderBook> orderbooks) {
        this.isEndOfDay = true;
        List<String> logList = new ArrayList<>();
        tsb.computeTimestampForCurrentTick();
        long timestampForCurrentTick = tsb.getTimestampForCurrentTick();
        String dateTime = tsb.getDateTimeForCurrentTick();

        for (OrderBook ob : orderbooks)
            logList.add(addEventTimeToLog(timestampForCurrentTick, dateTime, atomBasicLogBuilder.day(numOfDay, ob)));

        for (String log : logList)
            sendLog(log, timestampForCurrentTick);

        tsb.incrementCurrentDay();
        this.isEndOfDay = false;
    }


    /**
     * Log the progress of the simulation
     */
    private void logSimulationProgression() {
        double progressionPercentage = ((double)tsb.getCurrentTick() / tsb.getNbTicksIntraday() * 100);
        boolean everyTenPercent = (progressionPercentage % 10) == 0;
        if ( everyTenPercent )
            LOGGER.debug("Day " + tsb.getCurrentDay() +" - Tick " + tsb.getCurrentTick() + " over " + tsb.getNbTicksIntraday() + "");
    }

    protected void sendLog(String log, long timestamp) {
        for (AtomDataInjector injector : injectors)
            injector.send(addProcessingTimeToLog(timestamp,log));
    }

}