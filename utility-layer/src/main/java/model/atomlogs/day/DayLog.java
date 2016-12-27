package model.atomlogs.day;

import model.atomlogs.AtomLog;
import utils.UtilityLayerException;

/**
 * @Author raphael on 22/12/2016.
 */
public class DayLog extends AtomLog {


    private static final int LOG_LENGTH = LogLengths.DAY_LOG.getLength();
    private int numDay;
    private String orderBookName;
    private long firstFixedPrice;
    private long lowestPrice;
    private long highestPrice;
    private long lastFixedPrice;
    private long nbTotalFixedPrice;

    public DayLog(String[] logParts) throws UtilityLayerException {
        super(logParts);
    }

    @Override
    protected void construtFromLog() throws UtilityLayerException  {
        super.construtFromLog();
        checkLogComplete(LOG_LENGTH);
        this.numDay = Integer.parseInt(logParts[DayLogIndexes.NUM_DAY.getIndex()]);
        this.orderBookName = logParts[DayLogIndexes.ORDER_BOOK_NAME.getIndex()];
        this.firstFixedPrice = Long.parseLong(logParts[DayLogIndexes.FIRST_FIXED_PRICE.getIndex()]);
        this.lowestPrice = Long.parseLong(logParts[DayLogIndexes.LOWEST_PRICE.getIndex()]);
        this.highestPrice = Long.parseLong(logParts[DayLogIndexes.HIGHEST_PRICE.getIndex()]);
        this.lastFixedPrice = Long.parseLong(logParts[DayLogIndexes.LAST_FIXED_PRICE.getIndex()]);
        this.nbTotalFixedPrice = Long.parseLong(logParts[DayLogIndexes.NB_TOTAL_FIXED_PRICE.getIndex()]);
    }

    public int getNumDay() {
        return numDay;
    }

    public String getOrderBookName() {
        return orderBookName;
    }

    public long getFirstFixedPrice() {
        return firstFixedPrice;
    }

    public long getLowestPrice() {
        return lowestPrice;
    }

    public long getHighestPrice() {
        return highestPrice;
    }

    public long getLastFixedPrice() {
        return lastFixedPrice;
    }

    public long getNbTotalFixedPrice() {
        return nbTotalFixedPrice;
    }

    private enum DayLogIndexes {
        NUM_DAY(1),
        ORDER_BOOK_NAME(2),
        FIRST_FIXED_PRICE(3),
        LOWEST_PRICE(4),
        HIGHEST_PRICE(5),
        LAST_FIXED_PRICE(6),
        NB_TOTAL_FIXED_PRICE(7);
        int index;
        DayLogIndexes(int i) {this.index = i;}
        public int getIndex() {return index;}
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(super.toString()).append(";")
                .append(numDay).append(";")
                .append(orderBookName).append(";")
                .append(firstFixedPrice).append(";")
                .append(lowestPrice).append(";")
                .append(highestPrice).append(";")
                .append(lastFixedPrice).append(";")
                .append(nbTotalFixedPrice)
                .toString();
    }
}
