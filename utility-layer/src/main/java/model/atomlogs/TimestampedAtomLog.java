package model.atomlogs;

import utils.UtilityLayerException;

import java.io.Serializable;
import java.util.Arrays;


/**
 * @Author raphael on 27/12/2016.
 *
 * Class that compose an AtomLog and two fields for the timestamp (long and dateTime format)
 */
public class TimestampedAtomLog implements Serializable {

    private AtomLog atomLog;
    private long timeStamp;
    private String dateTime;
    boolean withDateTime;


    /**
     * Construct a TimestampedAtomLog with the timestamp and with the date time if the log has
     * one, without it otherwise.
     * @param log the entire log
     * @param withDateTime true if the log comes with a date time in addition to the long timestamp
     */
    public TimestampedAtomLog(String log, boolean withDateTime) {
        this.withDateTime = withDateTime;
        this.constructTimestampedLog(log);
    }


    /**
     * Extract the timestamp and the dateTime (if present) and build the AtomLog with the rest of
     * the log information
     * @param log the entire log
     */
    private void constructTimestampedLog(String log) {
        if (log == null || log.equals(""))
            throw new UtilityLayerException("Impossible to construct AtomLog from empty log");
        String[] logParts = log.split(AtomLogFactory.ATOM_LOG_SEPARATOR);
        this.timeStamp = Long.parseLong(logParts[TimestampedAtomLogIndexes.TIMESTAMP.getIndex()]);

        int startIndex = withDateTime
                ? TimestampedAtomLogIndexes.DATETIME.getIndex() + 1
                : TimestampedAtomLogIndexes.TIMESTAMP.getIndex() + 1;
        this.dateTime = withDateTime
                ? logParts[TimestampedAtomLogIndexes.DATETIME.getIndex()]
                : "";
        this.atomLog = AtomLogFactory.createAtomLog(Arrays.copyOfRange(logParts, startIndex, logParts.length));
    }


    public AtomLog getAtomLog() {
        return atomLog;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getDateTime() {
        return dateTime;
    }

    public boolean isWithDateTime() {
        return withDateTime;
    }

    enum TimestampedAtomLogIndexes {
        TIMESTAMP(0),
        DATETIME(1);
        int index;
        TimestampedAtomLogIndexes(int i) {this.index = i;}
        public int getIndex() {return index;}
    }
}
