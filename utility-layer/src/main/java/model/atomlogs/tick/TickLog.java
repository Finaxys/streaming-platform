package model.atomlogs.tick;

import model.atomlogs.AtomLog;
import utils.UtilityLayerException;

/**
 * @Author raphael on 22/12/2016.
 */
public class TickLog extends AtomLog {


    private static final int LOG_LENGTH = LogLengths.TICK_LOG.getLength();
    private int numTick;
    private String orderBookName;
    private long bestAskPrice;
    private long bestBidPrice;
    private long lastPrice;

    public TickLog(String[] logParts) throws UtilityLayerException {
        super(logParts);
    }

    @Override
    protected void construtFromLog() throws UtilityLayerException  {
        super.construtFromLog();
        checkLogComplete(LOG_LENGTH);
        this.numTick = Integer.parseInt(logParts[TickLogIndexes.NUM_TICK.getIndex()]);
        this.orderBookName = logParts[TickLogIndexes.ORDERBOOK_NAME.getIndex()];
        this.bestAskPrice = Long.parseLong(logParts[TickLogIndexes.BEST_ASK_PRICE.getIndex()]);
        this.bestBidPrice = Long.parseLong(logParts[TickLogIndexes.BEST_BID_PRICE.getIndex()]);
        this.lastPrice = Long.parseLong(logParts[TickLogIndexes.LAST_PRICE.getIndex()]);
    }

    public int getNumTick() {
        return numTick;
    }

    public String getOrderBookName() {
        return orderBookName;
    }

    public long getBestAskPrice() {
        return bestAskPrice;
    }

    public long getBestBidPrice() {
        return bestBidPrice;
    }

    public long getLastPrice() {
        return lastPrice;
    }

    private enum TickLogIndexes {
        NUM_TICK(1),
        ORDERBOOK_NAME(2),
        BEST_ASK_PRICE(3),
        BEST_BID_PRICE(4),
        LAST_PRICE(5);
        int index;
        TickLogIndexes(int i) {this.index = i;}
        public int getIndex() {return index;}
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(super.toString()).append(";")
                .append(numTick).append(";")
                .append(orderBookName).append(";")
                .append(bestAskPrice).append(";")
                .append(bestBidPrice).append(";")
                .append(lastPrice)
                .toString();
    }
}
