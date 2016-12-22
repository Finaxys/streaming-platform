package model.atomlogs.orders;

import utils.UtilityLayerException;

/**
 * @Author raphael on 22/12/2016.
 */
public class CancelUpdateOrderLog extends OrderLog {

    private static final int LOG_LENGTH = LogLengths.CANCEL_UPDATE_ORDER_LOG.getLength();

    private String orderIdToActOn;

    public CancelUpdateOrderLog(String[] logParts) throws UtilityLayerException {
        super(logParts);
    }

    @Override
    protected void construtFromLog() throws UtilityLayerException {
        super.construtFromLog();
        checkLogComplete(LOG_LENGTH);
        this.orderIdToActOn = logParts[CancelUpdateOrderLogIndexes.ORDER_ID_TO_ACT_ON.getIndex()];
    }

    public String getOrderIdToActOn() {
        return orderIdToActOn;
    }


    protected enum CancelUpdateOrderLogIndexes {
        ORDER_ID_TO_ACT_ON(5);
        int index;
        CancelUpdateOrderLogIndexes(int i) {this.index = i;}
        public int getIndex() {return index;}
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(super.toString()).append(";")
                .append(orderIdToActOn)
                .toString();
    }


}
