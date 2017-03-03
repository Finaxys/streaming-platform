package com.finaxys.flink.function;

import model.atomlogs.TimestampedAtomLog;
import org.apache.flink.api.common.functions.FilterFunction;

/**
 * @Author raphael on 01/03/2017.
 */
public class AtomLogFilter implements FilterFunction<TimestampedAtomLog> {

    private String filterFor;

    public AtomLogFilter(String filterFor) {
        this.filterFor = filterFor;
    }

    @Override
    public boolean filter(TimestampedAtomLog timestampedAtomLog) throws Exception {
        String logType = timestampedAtomLog.getAtomLog().getLogType();
        return logType.equals(filterFor);
    }
}
