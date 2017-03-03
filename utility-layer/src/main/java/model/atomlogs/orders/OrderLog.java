package model.atomlogs.orders;

import model.atomlogs.AtomLog;
import model.atomlogs.AtomLogFactory;
import utils.UtilityLayerException;

/**
 * @Author raphael on 22/12/2016.
 */
public class OrderLog extends AtomLog {

    private static final int LOG_LENGTH = LogLengths.ORDER_LOG.getLength();
    protected String orderBookName;
    protected String agentSenderName;
    protected String orderId;
    protected String orderType;

    public OrderLog(String[] logParts) throws UtilityLayerException {
        super(logParts);
    }

    @Override
    protected void construtFromLog() throws UtilityLayerException  {
        super.construtFromLog();
        checkLogComplete(LOG_LENGTH);

        this.orderBookName = logParts[OrderLogIndexes.ORDERBOOK_NAME.getIndex()];
        this.agentSenderName = logParts[OrderLogIndexes.AGENT_SENDER_NAME.getIndex()];
        this.orderId = logParts[OrderLogIndexes.ORDER_ID.getIndex()];
        this.orderType = logParts[OrderLogIndexes.ORDER_TYPE.getIndex()];
    }

    public String getOrderBookName() {
        return orderBookName;
    }

    public String getAgentSenderName() {
        return agentSenderName;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderType() {
        return orderType;
    }


    public boolean isCancelOrder() {
        return orderType.toUpperCase().equals(OrderTypes.CANCEL_ORDER.getCode());
    }
    public boolean isIcebergOrder() {
        return orderType.toUpperCase().equals(OrderTypes.ICEBERG_ORDER.getCode());
    }
    public boolean isLimitOrder() {
        return orderType.toUpperCase().equals(OrderTypes.LIMIT_ORDER.getCode());
    }
    public boolean isMarketOrder() {
        return orderType.toUpperCase().equals(OrderTypes.MARKET_ORDER.getCode());
    }
    public boolean isUpdateOrder() {
        return orderType.toUpperCase().equals(OrderTypes.UPDATE_ORDER.getCode());
    }


    protected enum OrderLogIndexes {
        ORDERBOOK_NAME(1),
        AGENT_SENDER_NAME(2),
        ORDER_ID(3),
        ORDER_TYPE(4);
        int index;
        OrderLogIndexes(int i) {this.index = i;}
        public int getIndex() {return index;}
    }

    public enum OrderTypes {
        CANCEL_ORDER("C"),
        ICEBERG_ORDER("I"),
        LIMIT_ORDER("L"),
        MARKET_ORDER("M"),
        UPDATE_ORDER("U");
        String code;
        OrderTypes(String code) {this.code = code;}
        public String getCode() {return code;}
    }

    public enum OrderDirections {
        SELLING("A"),
        BUYING("B");
        String code;
        OrderDirections(String code) {this.code = code;}
        public String getCode() {return code;}
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(super.toString()).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(orderBookName).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(agentSenderName).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(orderId).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(orderType)
                .toString();
    }
}
