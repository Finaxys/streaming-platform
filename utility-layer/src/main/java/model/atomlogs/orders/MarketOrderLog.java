package model.atomlogs.orders;

import model.atomlogs.AtomLogFactory;
import utils.UtilityLayerException;

/**
 * @Author raphael on 22/12/2016.
 */
public class MarketOrderLog extends OrderLog {

    private static final int LOG_LENGTH = LogLengths.MARKET_ORDER_LOG.getLength();

    private String direction;
    private int quantity;
    private long validity;


    public MarketOrderLog(String[] logParts) throws UtilityLayerException {
        super(logParts);
    }

    @Override
    protected void construtFromLog() throws UtilityLayerException {
        super.construtFromLog();
        checkLogComplete(LOG_LENGTH);
        this.direction = logParts[MarketOrderLogIndexes.DIRECTION.getIndex()];
        this.quantity = Integer.parseInt(logParts[MarketOrderLogIndexes.QUANTITY.getIndex()]);
        this.validity = Long.parseLong(logParts[MarketOrderLogIndexes.VALIDITY.getIndex()]);
    }

    public String getDirection() {
        return direction;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getValidity() {
        return validity;
    }

    protected enum MarketOrderLogIndexes {
        DIRECTION(5),
        QUANTITY(6),
        VALIDITY(7);
        int index;
        MarketOrderLogIndexes(int i) {this.index = i;}
        public int getIndex() {return index;}
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(super.toString()).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(direction).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(quantity).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(validity)
                .toString();
    }
}
