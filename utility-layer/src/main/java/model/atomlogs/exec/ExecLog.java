package model.atomlogs.exec;

import model.atomlogs.AtomLog;
import model.atomlogs.AtomLogFactory;
import utils.UtilityLayerException;

/**
 * @Author raphael on 22/12/2016.
 */
public class ExecLog extends AtomLog {

    private static final int LOG_LENGTH = LogLengths.EXEC_LOG.getLength();
    private String agentSenderNameAndOrderId;

    public ExecLog(String[] logParts) throws UtilityLayerException {
        super(logParts);
    }

    @Override
    protected void construtFromLog() throws UtilityLayerException  {
        super.construtFromLog();
        checkLogComplete(LOG_LENGTH);
        this.agentSenderNameAndOrderId = logParts[ExecLogIndexes.AGENT_SENDER_NAME_AND_ORDER_ID.getIndex()];
    }

    public String getAgentSenderNameAndOrderId() {
        return agentSenderNameAndOrderId;
    }

    private enum ExecLogIndexes {
        AGENT_SENDER_NAME_AND_ORDER_ID(1);
        int index;
        ExecLogIndexes(int i) {this.index = i;}
        public int getIndex() {return index;}
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(super.toString()).append(AtomLogFactory.ATOM_LOG_SEPARATOR)
                .append(agentSenderNameAndOrderId)
                .toString();
    }
}
