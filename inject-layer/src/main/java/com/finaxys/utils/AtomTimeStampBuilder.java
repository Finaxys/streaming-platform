package com.finaxys.utils;

import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AtomTimeStampBuilder {

    private static Logger LOGGER = Logger.getLogger(TimeStampBuilderOld.class);

    // Useful constants used to build a timestamp
    private static final String TIME_FORMAT = "h:mm"; //TODO why use hours and minutes and not seconds ?
    private static final String DATE_FORMAT = "mm/dd/yyyy";
    private static final long NB_MILLI_SEC_PER_DAY = 86400000;
    private static final long NB_MILLI_SEC_PER_HOUR = 3600000;

    // simulation properties from AtomInjectConfiguration
    private int nbAgents;
    private int nbOrderBooks;
    private int nbTicksIntraday;

    // variables obtained from simulation properties
    private long dateBeginInMillis = 0L;
    private long marketOpenHourInMillis = 0L;
    private long marketCloseHourInMillis = 0L;

    // iterate over ticks and days
    private int currentTick = 1;
    private int currentDay = 0;

    // calculated parameters used to build the timestamp
    private long nbMillisPerTick;
    private long nbMaxOrdersPerTick;
    private long nbMillisPerOrder;

    // the result of calculation : simulate a timestamp
    private long timeStamp;


    public AtomTimeStampBuilder(String dateBegin, String marketOpenHour,
                                String marketCloseHour, int nbTicksIntraday,
                                int nbAgents, int nbOrderBooks) {
        this.nbTicksIntraday = nbTicksIntraday;
        this.nbAgents = nbAgents;
        this.nbOrderBooks = nbOrderBooks;

        transformBeginDateAndOpeningClosingHoursInMillis(dateBegin, marketOpenHour, marketCloseHour);

        computeParametersUsedToBuildTimestamp();

        this.timeStamp = computeTimestampForCurrentTick();
    }



    private void transformBeginDateAndOpeningClosingHoursInMillis(
            String dateBegin, String marketOpenHour, String marketCloseHour) {

        try {
            /* use of SimpleDateFormat objects to transform strings into
               Date objects to then get the number of millis from the date */
            SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
            SimpleDateFormat timeFormatter = new SimpleDateFormat(TIME_FORMAT);

            /* get the number of millis corresponding to the market begin date
               and the opening/closure hours */
            dateBeginInMillis = dateFormatter.parse(dateBegin).getTime();
            marketOpenHourInMillis = timeFormatter.parse(marketOpenHour).getTime();
            marketCloseHourInMillis = timeFormatter.parse(marketCloseHour).getTime();

            // assert that the conversion in millis is completed
            boolean convertionInMillisCompleted = dateBeginInMillis != 0L
                    && marketOpenHourInMillis != 0L
                    && marketCloseHourInMillis != 0L;
            assert convertionInMillisCompleted;


        } catch (ParseException e) {
            throw new InjectLayerException(
                    "Cannot convert market begin date and opening/closure hours to milliseconds ", e);
        }
    }


    private void computeParametersUsedToBuildTimestamp() {
        // compute the number of milliseconds far a single tick
        long nbMillisInAMarketDay = marketCloseHourInMillis - marketOpenHourInMillis;
        nbMillisPerTick = nbMillisInAMarketDay / nbTicksIntraday;
        LOGGER.info("Number of milliseconds per Tick = " + nbMillisPerTick);
        // +1 to not reach the closehour on the last tick or not +1 but begin at open hour //TODO Demander a Mehdi ce que ce commentaire veut dire

        // compute the maximum number of orders that can be issued between two ticks
        nbMaxOrdersPerTick = nbAgents * nbOrderBooks * 2;
        LOGGER.info("Max number of orders by tick (nbMaxOrdersPerTick) = " + nbMaxOrdersPerTick);

        // compute the average time needed to send one single order
        nbMillisPerOrder = (nbMillisPerTick / nbMaxOrdersPerTick);
        LOGGER.info("Average time needed to send one single order (nbMillisPerOrder) = " + nbMillisPerOrder);
    }


    public long computeTimestampForCurrentTick() {
        long timeStampCurrentTick;

        if (currentTick == nbTicksIntraday) {
            timeStampCurrentTick = NB_MILLI_SEC_PER_HOUR //TODO pourquoi on ajoute 1h au timestamp (NB_MILLI_SEC_PER_HOUR)
                    + dateBeginInMillis
                    + (currentDay - 1) * NB_MILLI_SEC_PER_DAY
                    + marketOpenHourInMillis
                    + (currentTick - 1) * nbMillisPerTick;
        }
        else {
            timeStampCurrentTick = NB_MILLI_SEC_PER_HOUR //TODO pourquoi on ajoute 1h au timestamp (NB_MILLI_SEC_PER_HOUR)
                    + dateBeginInMillis
                    + currentDay * NB_MILLI_SEC_PER_DAY
                    + marketOpenHourInMillis
                    + (currentTick - 1) * nbMillisPerTick;
        }
        return timeStampCurrentTick;
    }


    public long incrementTimeStampByOrderMillis() {
        timeStamp += nbMillisPerOrder;
        return timeStamp;
    }







    public int getNbAgents() {
        return nbAgents;
    }

    public int getNbOrderBooks() {
        return nbOrderBooks;
    }

    public int getNbTicksIntraday() {
        return nbTicksIntraday;
    }

    public void setNbAgents(int nbAgents) {
        this.nbAgents = nbAgents;
    }

    public void setNbOrderBooks(int nbOrderBooks) {
        this.nbOrderBooks = nbOrderBooks;
    }

    public void setNbTicksIntraday(int nbTicksIntraday) {
        this.nbTicksIntraday = nbTicksIntraday;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getNbMillisPerOrder() {
        return nbMillisPerOrder;
    }

    public long getDateBeginInMillis() {
        return dateBeginInMillis;
    }

    public long getMarketOpenHourInMillis() {
        return marketOpenHourInMillis;
    }

    public long getMarketCloseHourInMillis() {
        return marketCloseHourInMillis;
    }

    public long getNbMaxOrdersPerTick() {
        return nbMaxOrdersPerTick;
    }

    public long getNbMillisPerTick() {
        return nbMillisPerTick;
    }

    public void setNbMillisPerTick(long nbMillisPerTick) {
        this.nbMillisPerTick = nbMillisPerTick;
    }

}