package model.atomlogs.orders;

import model.atomlogs.AtomLog;
import utils.UtilityLayerException;

/**
 * @Author raphael on 22/12/2016.
 */
public class OrderLogFactory {


    public static OrderLog createOrderLog(String[] logParts) throws UtilityLayerException {
        if (logParts.length < AtomLog.LogLengths.ORDER_LOG.getLength())
            throw new UtilityLayerException("Impossible to construct OrderLog : log is too short");

        return getOrderLog(logParts);
    }

    private static OrderLog getOrderLog(String[] logParts) throws UtilityLayerException {
        String orderType = logParts[OrderLog.OrderLogIndexes.ORDER_TYPE.getIndex()].toUpperCase();

        if (orderType.equals(OrderLog.OrderTypes.CANCEL_ORDER.getCode()))
            return new CancelUpdateOrderLog(logParts);

        if (orderType.equals(OrderLog.OrderTypes.UPDATE_ORDER.getCode()))
            return new CancelUpdateOrderLog(logParts);

        if (orderType.equals(OrderLog.OrderTypes.ICEBERG_ORDER.getCode()))
            return new IcebergOrderLog(logParts);

        if (orderType.equals(OrderLog.OrderTypes.LIMIT_ORDER.getCode()))
            return new LimitOrderLog(logParts);

        if (orderType.equals(OrderLog.OrderTypes.MARKET_ORDER.getCode()))
            return new MarketOrderLog(logParts);

        throw new UtilityLayerException("Impossible to construct OrderLog : order type unknown");
    }

}
