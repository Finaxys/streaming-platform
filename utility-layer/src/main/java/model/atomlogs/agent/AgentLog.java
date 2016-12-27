package model.atomlogs.agent;

import model.atomlogs.AtomLog;
import utils.UtilityLayerException;

/**
 * @Author raphael on 22/12/2016.
 */
public class AgentLog extends AtomLog {

    private static final int LOG_LENGTH = LogLengths.AGENT_LOG.getLength();
    private String agentName;
    private long cash;
    private String orderBookName;
    private int nbInvest;
    private long lastFixedPrice;

    public AgentLog(String[] logParts) throws UtilityLayerException {
        super(logParts);
    }

    @Override
    protected void construtFromLog() throws UtilityLayerException  {
        super.construtFromLog();
        checkLogComplete(LOG_LENGTH);
        this.agentName = logParts[AgentLogIndexes.AGENT_NAME.getIndex()];
        this.cash = Long.parseLong(logParts[AgentLogIndexes.CASH.getIndex()]);
        this.orderBookName = logParts[AgentLogIndexes.ORDERBOOK_NAME.getIndex()];
        this.nbInvest = Integer.parseInt(logParts[AgentLogIndexes.NB_INVEST.getIndex()]);
        this.lastFixedPrice = Long.parseLong(logParts[AgentLogIndexes.LAST_FIXED_PRICE.getIndex()]);
    }


    public String getAgentName() {
        return agentName;
    }

    public long getCash() {
        return cash;
    }

    public String getOrderBookName() {
        return orderBookName;
    }

    public int getNbInvest() {
        return nbInvest;
    }

    public long getLastFixedPrice() {
        return lastFixedPrice;
    }

    private enum AgentLogIndexes {
        AGENT_NAME(1),
        CASH(2),
        ORDERBOOK_NAME(3),
        NB_INVEST(4),
        LAST_FIXED_PRICE(5);
        int index;
        AgentLogIndexes(int i) {this.index = i;}
        public int getIndex() {return index;}
    }
}
