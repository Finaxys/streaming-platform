package com.finaxys.utils;

import configuration.AtomSimulationConfiguration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.TimeUnit;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * This class purpose is to build coherent "event-time" timestamps for each event that happen
 * in an ATOM simulation.
 *
 * Here is how ATOM works :
 *   - ATOM is punctuated by a tick round of talk.
 *   - At each round, all the agents are asked randomly for a decision. During two different
 *   ticks it is never the same sequence of talk.
 *   - The simulation run method have 2 parameters :
 *      1. How a day is composed
 *          - the number of ticks for opening period (which can of course be 0),
 *          - the number of ticks for the continuous period
 *          - the number of ticks for the closing period of each day
 *      2. The number of days for this experience.
 *
 * Here is the semantics of the ATOM simulation logs
 *   - Agent log : agent;name;cash;obName;nbInvest;lastFixedPrice
 *   - Exec log : exec;nameOfTheAgentThatSendTheOrder-OrderExtId
 *   - Order log : order;obName;sender;extId;type;dir;price;quty;valid
 *   - Price log : price;obName;price;executedQuty;dir;order1;order2;bestask;bestbid
 *   - Tick log : tick;numTick;obName;bestask;bestbid;lastPrice
 *
 * We need to stamp those logs with an "event-time" timestamp in order to make sense of them.
 * What we mean by event-time is "real world time" and not the processing time.
 *
 * At the moment, only Intraday events are timestamped
 *
 *
 */
public class AtomTimeStampBuilder {

    private static Logger LOGGER = LogManager.getLogger(AtomTimeStampBuilder.class);

    // Useful constants used to build a timestamp
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final Long NB_MILLI_SEC_PER_DAY = TimeUnit.DAYS.toMillis(1);
    private static final Long NB_MILLI_SEC_PER_SECOND = TimeUnit.SECONDS.toMillis(1);

    // simulation properties from AtomSimulationConfiguration
    private Integer nbAgents;
    private Integer nbOrderBooks;
    private Integer nbDays;
    private Integer nbTicksIntraday;
    private Integer nbTicksOpeningPeriod;
    private Integer nbTicksClosingPeriod;
    private Integer firstTickIntraday;
    private Integer lastTickIntraday;

    // variables obtained from simulation properties
    private Long dateBeginInMillis = 0L;
    private Long marketOpenHourInMillis = 0L;
    private Long marketCloseHourInMillis = 0L;

    // iterate over ticks and days
    private Integer currentTick = 1;
    private Integer currentDay = 1;

    // calculated parameters used to build the timestamp
    private Long nbMillisPerTick;
    private Long nbMaxOrdersPerTick;
    private Long nbMillisPerOrder;
    private DateTimeFormatter dateFormatter;
    private DateTimeFormatter timeFormatter;
    private DateTimeFormatter dateTimeFormatter;

    // the result of calculation : simulate a timestamp
    private Long eventTimeTimestamp;
    private String dateTimeCurrentTick;


    public AtomTimeStampBuilder(AtomSimulationConfiguration conf) {
        this.nbTicksOpeningPeriod = conf.getTickOpening();
        this.nbTicksClosingPeriod = conf.getTickClosing();
        this.nbTicksIntraday = conf.getTickContinuous();
        this.nbAgents = conf.getNbAgents();
        this.nbOrderBooks = conf.getNbOrderBooks();
        this.nbDays = conf.getDays();
        transformBeginDateAndOpeningClosingHoursInMillis(conf.getTsbTimeZone(), conf.getTsbDateBegin(), conf.getTsbOpenHour(), conf.getTsbCloseHour());
        computeParametersUsedToBuildTimestamp();
    }



    private void transformBeginDateAndOpeningClosingHoursInMillis(
            String timeZoneId, String dateBegin,
            String marketOpenHour, String marketCloseHour) throws InjectLayerException {

        try {
            /* use of SimpleDateFormat objects to transform strings into
               Date objects to then get the number of millis from the date */
            this.dateFormatter = DateTimeFormat.forPattern(DATE_FORMAT).withZoneUTC();
            this.timeFormatter = DateTimeFormat.forPattern(TIME_FORMAT).withZoneUTC();
            this.dateTimeFormatter = DateTimeFormat.forPattern(DATE_FORMAT + " " + TIME_FORMAT).withZoneUTC();
            // get the number of millis corresponding to the market begin date and the opening/closure hours
            dateBeginInMillis = dateFormatter.parseMillis(dateBegin);
            marketOpenHourInMillis = timeFormatter.parseMillis(marketOpenHour);
            marketCloseHourInMillis = timeFormatter.parseMillis(marketCloseHour);

            // assert that the conversion in millis is completed
            boolean convertionInMillisCompleted = dateBeginInMillis != 0L
                    && marketOpenHourInMillis != 0L
                    && marketCloseHourInMillis != 0L;
            assert convertionInMillisCompleted;
        }
        catch (Exception e) {
            throw new InjectLayerException(
                    "Cannot convert market begin date (value=" + dateBegin + ")" +
                            " and/or opening/closure hours (value=" + marketOpenHour + "/" + marketCloseHour + ")" +
                            " to milliseconds ", e);
        }
    }


    private void computeParametersUsedToBuildTimestamp() {
        /* Compute the number of milliseconds for a single tick
         *   We subtract two seconds (one for the beginning and one for the end of the day)
         *   so that the events occurring in the intraday period do not have the opening time
         *   nor the closing time timestamp*/
        Long nbMillisInAMarketDay = (marketCloseHourInMillis - marketOpenHourInMillis) - (NB_MILLI_SEC_PER_SECOND * 2);
        nbMillisPerTick = nbMillisInAMarketDay / nbTicksIntraday; // Lost of precision may occur

        // compute the maximum number of orders that can be issued between two ticks
        nbMaxOrdersPerTick = nbAgents.longValue() * nbOrderBooks.longValue(); // used to be multiplied by two but this isn't necessary

        // compute the average time needed to send one single order
        nbMillisPerOrder = (nbMillisPerTick / nbMaxOrdersPerTick); // Lost of precision may occur

        // set the lower and upper boundaries for the intraday ticks
        this.firstTickIntraday = nbTicksOpeningPeriod + 1;
        this.lastTickIntraday = nbTicksIntraday + nbTicksOpeningPeriod;
    }


    public void computeTimestampForCurrentTick() {
        long timeStampCurrentTick;

        // If we are in the pre-opening period
        if (currentTick < firstTickIntraday) {
            timeStampCurrentTick =
                    dateBeginInMillis
                    + (currentDay - 1) * NB_MILLI_SEC_PER_DAY
                    + marketOpenHourInMillis;
        }
        // If we are in the closing period
        else if (currentTick > lastTickIntraday) {
            timeStampCurrentTick =
                    dateBeginInMillis
                    + (currentDay - 1) * NB_MILLI_SEC_PER_DAY
                    + marketCloseHourInMillis;
        }
        // If we are in intraday
        else {
            timeStampCurrentTick =
                    dateBeginInMillis
                    + (currentDay - 1) * NB_MILLI_SEC_PER_DAY
                    + marketOpenHourInMillis
                    + NB_MILLI_SEC_PER_SECOND // Add one second to have a timestamp different from the beginning of the day
                    + (currentTick - firstTickIntraday) * nbMillisPerTick; // Begin the first tick intraday at the timestamp "marketOpenHourInMillis + 1 second"
        }
        this.eventTimeTimestamp = timeStampCurrentTick;
    }

    public long getTimestampForCurrentTick() {
        return this.eventTimeTimestamp;
    }

    public String getDateTimeForCurrentTick() {
        return this.dateTimeFormatter.print(eventTimeTimestamp);
    }



    public void setTimestampForPreOpening() {
        this.eventTimeTimestamp =  dateBeginInMillis
                + (currentDay - 1) * NB_MILLI_SEC_PER_DAY
                + marketOpenHourInMillis;
    }




    public Integer getNbAgents() {
        return nbAgents;
    }

    public Integer getNbOrderBooks() {
        return nbOrderBooks;
    }

    public Integer getNbTicksIntraday() {
        return nbTicksIntraday;
    }

    public void setNbAgents(Integer nbAgents) {
        this.nbAgents = nbAgents;
    }

    public void setNbOrderBooks(Integer nbOrderBooks) {
        this.nbOrderBooks = nbOrderBooks;
    }

    public void setNbTicksIntraday(Integer nbTicksIntraday) {
        this.nbTicksIntraday = nbTicksIntraday;
    }

    public Integer getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(Integer currentTick) {
        this.currentTick = currentTick;
    }

    public Integer getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(Integer currentDay) {
        this.currentDay = currentDay;
    }

    public Long getNbMillisPerOrder() {
        return nbMillisPerOrder;
    }

    public Long getDateBeginInMillis() {
        return dateBeginInMillis;
    }

    public Long getMarketOpenHourInMillis() {
        return marketOpenHourInMillis;
    }

    public Long getMarketCloseHourInMillis() {
        return marketCloseHourInMillis;
    }

    public Long getNbMaxOrdersPerTick() {
        return nbMaxOrdersPerTick;
    }

    public Long getNbMillisPerTick() {
        return nbMillisPerTick;
    }

    public void setNbMillisPerTick(Long nbMillisPerTick) {
        this.nbMillisPerTick = nbMillisPerTick;
    }

    public void incrementCurrentTick() throws InjectLayerException {
        int totalTicks = nbTicksOpeningPeriod + nbTicksIntraday + nbTicksClosingPeriod;
        if ( ! currentTick.equals(totalTicks))
            this.currentTick++;
    }

    public void incrementCurrentDay() throws InjectLayerException  {
        if (! currentDay.equals(nbDays)) {
            this.currentDay++;
            this.currentTick = 1;
        }
    }

    @Override
    public String toString() {
        return "AtomTimeStampBuilder{" + "\n" +
                "nbAgents=" + nbAgents + "\n" +
                ", nbOrderBooks=" + nbOrderBooks + "\n" +
                ", nbDays=" + nbDays + "\n" +
                ", nbTicksIntraday=" + nbTicksIntraday + "\n" +
                ", nbTicksOpeningPeriod=" + nbTicksOpeningPeriod + "\n" +
                ", nbTicksClosingPeriod=" + nbTicksClosingPeriod + "\n" +
                ", firstTickIntraday=" + firstTickIntraday + "\n" +
                ", lastTickIntraday=" + lastTickIntraday + "\n" +
                ", dateBeginInMillis=" + dateBeginInMillis + "\n" +
                ", marketOpenHourInMillis=" + marketOpenHourInMillis + "\n" +
                ", marketCloseHourInMillis=" + marketCloseHourInMillis + "\n" +
                ", currentTick=" + currentTick + "\n" +
                ", currentDay=" + currentDay + "\n" +
                ", nbMillisPerTick=" + nbMillisPerTick + "\n" +
                ", nbMaxOrdersPerTick=" + nbMaxOrdersPerTick + "\n" +
                ", nbMillisPerOrder=" + nbMillisPerOrder + "\n" +
                ", dateFormatter=" + dateFormatter + "\n" +
                ", timeFormatter=" + timeFormatter + "\n" +
                ", dateTimeFormatter=" + dateTimeFormatter + "\n" +
                ", eventTimeTimestamp=" + eventTimeTimestamp + "\n" +
                ", dateTimeCurrentTick='" + dateTimeCurrentTick + '\'' + "\n" +
                '}';
    }
}