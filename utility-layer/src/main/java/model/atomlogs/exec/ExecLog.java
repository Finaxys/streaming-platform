package model.atomlogs.exec;

import model.atomlogs.AtomLog;
import utils.UtilityLayerException;

/**
 * @Author raphael on 22/12/2016.
 */
public class ExecLog extends AtomLog {

    private static final int LOG_LENGTH = LogLengths.EXEC_LOG.getLength();
    private String agentSenderName;
    private String orderId;

    public ExecLog(String[] logParts) throws UtilityLayerException {
        super(logParts);
    }

    @Override
    protected void construtFromLog() throws UtilityLayerException  {
        super.construtFromLog();
        checkLogComplete(LOG_LENGTH);
        this.agentSenderName = logParts[ExecLogIndexes.AGENT_SENDER_NAME.getIndex()];
        this.orderId = logParts[ExecLogIndexes.ORDER_ID.getIndex()];
    }

    public String getAgentSenderName() {
        return agentSenderName;
    }

    public String getOrderId() {
        return orderId;
    }

    private enum ExecLogIndexes {
        AGENT_SENDER_NAME(1),
        ORDER_ID(2);
        int index;
        ExecLogIndexes(int i) {this.index = i;}
        public int getIndex() {return index;}
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(super.toString()).append(";")
                .append(agentSenderName).append(";")
                .append(orderId)
                .toString();
    }
}
