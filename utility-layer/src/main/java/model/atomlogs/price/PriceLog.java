package model.atomlogs.price;

import model.atomlogs.AtomLog;
import model.atomlogs.AtomLogFactory;
import utils.UtilityLayerException;

/**
 * @Author raphael on 22/12/2016.
 */
public class PriceLog extends AtomLog {

    private static final int LOG_LENGTH = LogLengths.PRICE_LOG.getLength();
    private String orderBookName;
    private long price;
    private int executedQuty;
    private String direction;
    private String orderSourceID;
    private String orderMatchedID;
    private long bestAskPrice;
    private long bestBidPrice;

    public PriceLog(String[] logParts) throws UtilityLayerException {
        super(logParts);
    }

    @Override
    protected void construtFromLog() throws UtilityLayerException  {
        super.construtFromLog();
        checkLogComplete(LOG_LENGTH);
        this.orderBookName = logParts[PriceLogIndexes.ORDERBOOK_NAME.getIndex()];
        this.price = Long.parseLong(logParts[PriceLogIndexes.PRICE.getIndex()]);
        this.executedQuty = Integer.parseInt(logParts[PriceLogIndexes.EXECUTED_QUANTITY.getIndex()]);
        this.direction = logParts[PriceLogIndexes.DIRECTION.getIndex()];
        this.orderSourceID = logParts[PriceLogIndexes.ORDER_SOURCE_ID.getIndex()];
        this.orderMatchedID = logParts[PriceLogIndexes.ORDER_MATCHED_ID.getIndex()];
        this.bestAskPrice = Long.parseLong(logParts[PriceLogIndexes.BEST_ASK_PRICE.getIndex()]);
        this.bestBidPrice = Long.parseLong(logParts[PriceLogIndexes.BEST_BID_PRICE.getIndex()]);
    }

    public String getOrderBookName() {
        return orderBookName;
    }

    public long getPrice() {
        return price;
    }

    public int getExecutedQuty() {
        return executedQuty;
    }

    public String getDirection() {
        return direction;
    }

    public String getOrderSourceID() {
        return orderSourceID;
    }

    public String getOrderMatchedID() {
        return orderMatchedID;
    }

    public long getBestAskPrice() {
        return bestAskPrice;
    }

    public long getBestBidPrice() {
        return bestBidPrice;
    }

    private enum PriceLogIndexes {
        ORDERBOOK_NAME(1),
        PRICE(2),
        EXECUTED_QUANTITY(3),
        DIRECTION(4),
        ORDER_SOURCE_ID(5),
        ORDER_MATCHED_ID(6),
        BEST_ASK_PRICE(7),
        BEST_BID_PRICE(8);
        int index;
        PriceLogIndexes(int i) {this.index = i;}
        public int getIndex() {return index;}
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(super.toString()).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(orderBookName).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(price).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(executedQuty).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(direction).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(orderSourceID).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(orderMatchedID).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(bestAskPrice).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(bestBidPrice)
                .toString();
    }
}
