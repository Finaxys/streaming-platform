package model.atomlogs;

import model.atomlogs.agent.AgentLog;
import model.atomlogs.day.DayLog;
import model.atomlogs.exec.ExecLog;
import model.atomlogs.orders.OrderLogFactory;
import model.atomlogs.price.PriceLog;
import model.atomlogs.tick.TickLog;
import utils.UtilityLayerException;

/**
 * @Author raphael on 22/12/2016.
 */
public class AtomLogFactory {

    // FIXME gerer les timestamps long/dateTime
    public static final String ATOM_LOG_SEPARATOR = ";";

    public static AtomLog createAtomLog(String log) throws UtilityLayerException {

        if (log == null || log.equals(""))
            throw new UtilityLayerException("Impossible to construct AtomLog from empty log");
        String[] logParts = log.split(ATOM_LOG_SEPARATOR);

        return createAtomLog(logParts);
    }

    public static AtomLog createAtomLog(String[] logParts) throws UtilityLayerException {

        if (logParts.length == 0)
            throw new UtilityLayerException("Impossible to construct AtomLog from empty log");

        return getBasicAtomLog(logParts);
    }

    private static AtomLog getBasicAtomLog(String[] logParts) {
        String logType = logParts[AtomLog.BasicAtomLogIndexes.LOG_TYPE.getIndex()].toUpperCase();

        if (logType.equals(AtomLog.LogTypes.ORDER.getCode()))
            return OrderLogFactory.createOrderLog(logParts);

        if (logType.equals(AtomLog.LogTypes.EXEC.getCode()))
            return new ExecLog(logParts);

        if (logType.equals(AtomLog.LogTypes.AGENT.getCode()))
            return new AgentLog(logParts);

        if (logType.equals(AtomLog.LogTypes.PRICE.getCode()))
            return new PriceLog(logParts);

        if (logType.equals(AtomLog.LogTypes.TICK.getCode()))
            return new TickLog(logParts);

        if (logType.equals(AtomLog.LogTypes.DAY.getCode()))
            return new DayLog(logParts);

        throw new UtilityLayerException("Impossible to construct AtomLog : log type unknown");
    }

}
