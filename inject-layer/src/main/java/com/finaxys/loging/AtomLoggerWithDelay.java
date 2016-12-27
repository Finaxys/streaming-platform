package com.finaxys.loging;

import com.finaxys.loging.injectors.AtomDataInjector;
import com.finaxys.utils.AtomSimulationConfiguration;
import com.finaxys.utils.InjectLayerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author raphael on 27/12/2016.
 *
 * Class in charge of logging all messages produced by the ATOM simulation
 * and randomly adding some delay to random messages.
 *
 * The delay to add is defined in the configuration file, as well as the percentage
 * of delayed logs.
 */
public class AtomLoggerWithDelay extends AtomLogger {

    private static Logger LOGGER = LogManager.getLogger(AtomLoggerWithDelay.class);

    private long outOfOrderDelay;
    private Long outOfOrderMark;
    private Long actualLogTime;
    private int outOfOrderPercentage;
    private List<String> outOfOrderLogs;



    public AtomLoggerWithDelay(AtomSimulationConfiguration conf, AtomDataInjector... injectors) {
        super(conf, injectors);
        outOfOrderLogs = new ArrayList<>();
        this.outOfOrderPercentage = conf.getOutOfOrderPercentage();
        this.outOfOrderMark = null;
        computeOutOfOrderDelay(conf);
    }

    private void computeOutOfOrderDelay(AtomSimulationConfiguration conf) {
        this.outOfOrderDelay = super.tsb.getNbMillisPerTick() * conf.getOutOfOrderCoefficient();
        if (outOfOrderDelay > (conf.getOutOfOrderMaxDelayInMillies())) {
            this.outOfOrderDelay = conf.getOutOfOrderMaxDelayInMillies();
        }
        if (outOfOrderDelay < super.tsb.getNbMillisPerTick())
            throw new InjectLayerException("Wrong parameters for a simulation with out of order logs : the maximum delay is shorter than one single tick");

        LOGGER.debug("Out of order delay = " + outOfOrderDelay + " milliseconds see .AtomLoggerWithDelay.java");
    }


    /**
     * Method that send a log with a possible delay.
     * Given a certain percentage of out of order logs and the log timestamps,
     * this method will determine if the log must be sent with delay or right away.
     * This method is also in charge of sending all previously delayed logs when the
     * maximum delay duration is over.
     * @param log the log to send
     * @param timestamp the "event time" timestamp of the log
     */
    @Override
    protected void sendLog(String log, long timestamp) {
        // Init outOfOrderMark when first call to sendLog is made
        if (this.outOfOrderMark == null)
            this.outOfOrderMark = timestamp;

        this.actualLogTime = timestamp;

        if (isLogBeeingDelayed()) {
            outOfOrderLogs.add(log);
        }
        else {
            super.sendLog(log, timestamp);
        }

        if (isTimeToSendOutOfOrderLogs()) {
            super.sendLogs(outOfOrderLogs, timestamp);
            this.outOfOrderLogs.clear();
            this.outOfOrderMark = timestamp;
        }

    }

    /**
     * Method that log a list of logs
     * @see AtomLoggerWithDelay#sendLog(String, long)
     * @param logs the list of logs
     * @param timestamp the "event time" timestamp of the log
     */
    @Override
    protected void sendLogs(List<String> logs, long timestamp) {
        for (String log : logs)
            this.sendLog(log, timestamp);
    }


    /**
     * Method used to determine whether or not a log must be delayed
     * @return true if the current log have to be delayed, false otherwise
     */
    private boolean isLogBeeingDelayed() {
        int randomInt = ThreadLocalRandom.current().nextInt(0, 100 + 1);
        return randomInt <= outOfOrderPercentage;
    }

    /**
     * Method used to determine whether or not it's time to send all delayed logs
     * @return true if it's time to send delayed logs, false otherwise
     */
    private boolean isTimeToSendOutOfOrderLogs() {
        return actualLogTime >= ( outOfOrderMark + outOfOrderDelay );
    }
}
