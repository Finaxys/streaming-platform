package model.atomlogs.orders;

import model.atomlogs.AtomLogFactory;
import utils.UtilityLayerException;

/**
 * @Author raphael on 22/12/2016.
 */
public class LimitOrderLog extends OrderLog {

    private static final int LOG_LENGTH = LogLengths.LIMIT_ORDER_LOG.getLength();

    private String direction;
    private long price;
    private int quantity;
    private long validity;


    public LimitOrderLog(String[] logParts) throws UtilityLayerException {
        super(logParts);
    }

    @Override
    protected void construtFromLog() throws UtilityLayerException {
        super.construtFromLog();
        checkLogComplete(LOG_LENGTH);
        this.direction = logParts[LimitOrderLogIndexes.DIRECTION.getIndex()];
        this.price = Long.parseLong(logParts[LimitOrderLogIndexes.PRICE.getIndex()]);
        this.quantity = Integer.parseInt(logParts[LimitOrderLogIndexes.QUANTITY.getIndex()]);
        this.validity = Long.parseLong(logParts[LimitOrderLogIndexes.VALIDITY.getIndex()]);
    }

    public String getDirection() {
        return direction;
    }

    public long getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getValidity() {
        return validity;
    }


    protected enum LimitOrderLogIndexes {
        DIRECTION(5),
        PRICE(6),
        QUANTITY(7),
        VALIDITY(8);
        int index;
        LimitOrderLogIndexes(int i) {this.index = i;}
        public int getIndex() {return index;}
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(super.toString()).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(direction).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(price).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(quantity).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(validity)
                .toString();
    }

}
