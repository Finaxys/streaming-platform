package model.atomlogs.orders;

import model.atomlogs.AtomLogFactory;
import utils.UtilityLayerException;

/**
 * @Author raphael on 22/12/2016.
 */
public class IcebergOrderLog extends OrderLog {

    private static final int LOG_LENGTH = LogLengths.ICEBERG_ORDER_LOG.getLength();

    private String direction;
    private long price;
    private int part;
    private int initialQuantity;
    private long validity;


    public IcebergOrderLog(String[] logParts) throws UtilityLayerException {
        super(logParts);
    }

    @Override
    protected void construtFromLog() throws UtilityLayerException {
        super.construtFromLog();
        checkLogComplete(LOG_LENGTH);
        this.direction = logParts[IcebergOrderLogIndexes.DIRECTION.getIndex()];
        this.price = Long.parseLong(logParts[IcebergOrderLogIndexes.PRICE.getIndex()]);
        this.part = Integer.parseInt(logParts[IcebergOrderLogIndexes.PART.getIndex()]);
        this.initialQuantity = Integer.parseInt(logParts[IcebergOrderLogIndexes.INITIAL_QUANTITY.getIndex()]);
        this.validity = Long.parseLong(logParts[IcebergOrderLogIndexes.VALIDITY.getIndex()]);
    }


    public String getDirection() {
        return direction;
    }

    public long getPrice() {
        return price;
    }

    public int getPart() {
        return part;
    }

    public int getInitialQuantity() {
        return initialQuantity;
    }

    public long getValidity() {
        return validity;
    }



    protected enum IcebergOrderLogIndexes {
        DIRECTION(5),
        PRICE(6),
        PART(7),
        INITIAL_QUANTITY(8),
        VALIDITY(9);
        int index;
        IcebergOrderLogIndexes(int i) {this.index = i;}
        public int getIndex() {return index;}
    }


    @Override
    public String toString() {
        return new StringBuilder()
                .append(super.toString()).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(direction).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(price).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(part).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(initialQuantity).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(validity)
                .toString();
    }

}
