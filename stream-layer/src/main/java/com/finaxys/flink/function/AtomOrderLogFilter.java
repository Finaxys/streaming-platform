package com.finaxys.flink.function;

import com.finaxys.utils.StreamLayerException;
import model.atomlogs.AtomLog;
import model.atomlogs.TimestampedAtomLog;
import model.atomlogs.orders.OrderLog;
import org.apache.flink.api.common.functions.FilterFunction;

/**
 * @Author raphael on 01/03/2017.
 */
public class AtomOrderLogFilter implements FilterFunction<TimestampedAtomLog> {

    private String filterFor;

    public AtomOrderLogFilter(String filterFor) {
        this.filterFor = filterFor;
    }

    @Override
    public boolean filter(TimestampedAtomLog timestampedAtomLog) throws Exception {
        String logType = timestampedAtomLog.getAtomLog().getLogType();
        if (! logType.equals(AtomLog.LogTypes.ORDER.getCode()))
            throw new StreamLayerException("Log must be of Order type");
        return ((OrderLog) timestampedAtomLog.getAtomLog()).getOrderType().equals(filterFor);
    }
}
