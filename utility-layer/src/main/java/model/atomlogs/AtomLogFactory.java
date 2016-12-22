package model.atomlogs;

import model.atomlogs.agent.AgentLog;
import model.atomlogs.day.DayLog;
import model.atomlogs.exec.ExecLog;
import model.atomlogs.orders.OrderLog;
import model.atomlogs.orders.OrderLogFactory;
import model.atomlogs.price.PriceLog;
import model.atomlogs.tick.TickLog;
import utils.UtilityLayerException;

/**
 * @Author raphael on 22/12/2016.
 */
public class AtomLogFactory {

    private static final String LOG_SEPARATOR = ";";

    public static BasicAtomLog createAtomLog(String log) throws UtilityLayerException {

        if (log == null || log.equals(""))
            throw new UtilityLayerException("Impossible to construct BasicAtomLog from empty log");
        String[] logParts = log.split(LOG_SEPARATOR);

        if (logParts.length == 0)
            throw new UtilityLayerException("Impossible to construct BasicAtomLog from empty log");

        return getBasicAtomLog(logParts);
    }

    private static BasicAtomLog getBasicAtomLog(String[] logParts) {
        String logType = logParts[BasicAtomLog.BasicAtomLogIndexes.LOG_TYPE.getIndex()].toUpperCase();

        if (logType.equals(BasicAtomLog.LogTypes.ORDER.getCode()))
            return OrderLogFactory.createOrderLog(logParts);

        if (logType.equals(BasicAtomLog.LogTypes.EXEC.getCode()))
            return new ExecLog(logParts);

        if (logType.equals(BasicAtomLog.LogTypes.AGENT.getCode()))
            return new AgentLog(logParts);

        if (logType.equals(BasicAtomLog.LogTypes.PRICE.getCode()))
            return new PriceLog(logParts);

        if (logType.equals(BasicAtomLog.LogTypes.TICK.getCode()))
            return new TickLog(logParts);

        if (logType.equals(BasicAtomLog.LogTypes.DAY.getCode()))
            return new DayLog(logParts);

        throw new UtilityLayerException("Impossible to construct BasicAtomLog : log type unknown");
    }

}
