package com.finaxys.utils;

import org.junit.BeforeClass;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.*;

/**
 * @Author raphael on 11/08/2016.
 */
public class AtomTimeStampBuilderTest {
    /*

    private static final String TIME_FORMAT = "hh:mm:ss";
    private static final String DATE_FORMAT = "MM/dd/yyyy";

    private static final long NB_MILLI_SEC_PER_DAY = 86400000;
    private static final long NB_MILLI_SEC_PER_HOUR = 3600000;

    // simulation properties
    private static int nbAgents = 10;
    private static int nbOrderBooks = 10;
    private static int nbTicksIntraday = 100;
    private static String timeZoneId = "Europe/London";
    private static String dateBegin = "08/18/2016";
    private static String marketOpenHour = "09:00:00";
    private static String marketCloseHour = "17:30:00";

    // variables obtained from simulation properties
    private static long dateBeginInMillis = 0L;
    private static long marketOpenHourInMillis = 0L;
    private static long marketCloseHourInMillis = 0L;
    // calculated parameters used to build the timestamp
    private static long nbMillisPerTick;
    private static long nbMaxOrdersPerTick;
    private static long nbMillisPerOrder;

    // class under test
    private static AtomTimeStampBuilder timeStampBuilder;


    @BeforeClass
    public static void setUp() throws Exception {
        timeStampBuilder = new AtomTimeStampBuilder(
                timeZoneId, dateBegin, marketOpenHour, marketCloseHour,
                nbTicksIntraday, nbAgents, nbOrderBooks);

        // transform dates in milliseconds
        SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        dateBeginInMillis = dateFormatter.parse(dateBegin).getTime();

        SimpleDateFormat timeFormatter = new SimpleDateFormat(TIME_FORMAT);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        marketOpenHourInMillis = timeFormatter.parse(marketOpenHour).getTime();
        marketCloseHourInMillis = timeFormatter.parse(marketCloseHour).getTime();

        // compute the number of milliseconds far a single tick
        long nbMillisInAMarketDay = marketCloseHourInMillis - marketOpenHourInMillis;
        nbMillisPerTick = nbMillisInAMarketDay / nbTicksIntraday;

        // compute the maximum number of orders that can be issued between two ticks
        nbMaxOrdersPerTick = nbAgents * nbOrderBooks * 2;

        // compute the average time needed to send one single order
        nbMillisPerOrder = (nbMillisPerTick / nbMaxOrdersPerTick);
    }

    @Test
    public void transformBeginDateAndOpeningClosingHoursInMillisTest() throws Exception {
        assertThat(timeStampBuilder.getDateBeginInMillis(),
                is(dateBeginInMillis));
        assertThat(timeStampBuilder.getMarketOpenHourInMillis(),
                is(marketOpenHourInMillis));
        assertThat(timeStampBuilder.getMarketCloseHourInMillis(),
                is(marketCloseHourInMillis));
    }


    @Test
    public void computeParametersUsedToBuildTimestampTest() throws Exception {
        assertThat(timeStampBuilder.getNbMillisPerTick(),
                is(nbMillisPerTick));
        assertThat(timeStampBuilder.getNbMaxOrdersPerTick(),
                is(nbMaxOrdersPerTick));
        assertThat(timeStampBuilder.getNbMillisPerOrder(),
                is(nbMillisPerOrder));
    }


    @Test
    public void computeTimeStampForFirstIntradayTickTest() throws Exception {
        long computedTimestampShouldBe = dateBeginInMillis + marketOpenHourInMillis;
        timeStampBuilder.setCurrentDay(0);
        timeStampBuilder.setCurrentTick(1);
        assertThat(timeStampBuilder.computeTimestampForCurrentTick(),
                is(computedTimestampShouldBe));
    }

    */
}
