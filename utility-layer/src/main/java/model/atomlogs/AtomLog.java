package model.atomlogs;

import utils.UtilityLayerException;

import java.io.Serializable;

/**
 * @Author raphael on 22/12/2016.
 */
public abstract class AtomLog implements Serializable {

    private static final int LOG_LENGTH = LogLengths.BASIC_ATOM_LOG.getLength();

    protected String logType;
    protected String [] logParts;

    public AtomLog(String [] logParts) throws UtilityLayerException {
        this.logParts = logParts;
        this.construtFromLog();
    }

    protected void construtFromLog() {
        checkLogComplete(LOG_LENGTH);
        this.logType = logParts[BasicAtomLogIndexes.LOG_TYPE.getIndex()];
    }

    protected void checkLogComplete(int expectedLogSize) {
        if (logParts.length < expectedLogSize)
            throw new UtilityLayerException("Error when constructing log model : missing information inside the log");
    }


    public String getLogType() {
        return logType;
    }

    public String[] getLogParts() {
        return logParts;
    }

    public boolean isOrderLog() {
        return this.logType.toUpperCase().equals(LogTypes.ORDER.getCode());
    }

    public boolean isExecLog() {
        return this.logType.toUpperCase().equals(LogTypes.EXEC.getCode());
    }

    public boolean isAgentLog() {
        return this.logType.toUpperCase().equals(LogTypes.AGENT.getCode());
    }

    public boolean isPriceLog() {
        return this.logType.toUpperCase().equals(LogTypes.PRICE.getCode());
    }

    public boolean isTickLog() {
        return this.logType.toUpperCase().equals(LogTypes.TICK.getCode());
    }

    public boolean isDayLog() {
        return this.logType.toUpperCase().equals(LogTypes.DAY.getCode());
    }

    protected enum BasicAtomLogIndexes {
        LOG_TYPE(0);
        int index;
        BasicAtomLogIndexes(int i) {this.index = i;}
        public int getIndex() {return index;}
    }

    public enum LogLengths {
        BASIC_ATOM_LOG(1),
        ORDER_LOG(5),
        CANCEL_UPDATE_ORDER_LOG(6),
        ICEBERG_ORDER_LOG(10),
        LIMIT_ORDER_LOG(9),
        MARKET_ORDER_LOG(8),
        EXEC_LOG(3),
        AGENT_LOG(6),
        PRICE_LOG(9),
        TICK_LOG(6),
        DAY_LOG(8);
        int length;
        LogLengths(int i) {this.length = i;}
        public int getLength() {return length;}
    }

    protected enum LogTypes {
        ORDER("ORDER"),
        EXEC("EXEC"),
        AGENT("AGENT"),
        PRICE("PRICE"),
        TICK("TICK"),
        DAY("DAY");
        String code;
        LogTypes(String code) {this.code = code;}
        public String getCode() {return code;}
    }

    @Override
    public String toString() {
        return logType;
    }
}
