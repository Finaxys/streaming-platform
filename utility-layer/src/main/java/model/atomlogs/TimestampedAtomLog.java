package model.atomlogs;

import org.apache.logging.log4j.LogManager;
import utils.UtilityLayerException;

import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Pattern;


/**
 * @Author raphael on 27/12/2016.
 *
 * Class that compose an AtomLog and two fields for the timestamp (long and dateTime format)
 */
public class TimestampedAtomLog implements Serializable {

    private static org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(TimestampedAtomLog.class);
    private static final Pattern dateTimePattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}");

    private AtomLog atomLog;
    private long processingTimeTimeStamp;
    private long eventTimeTimeStamp;
    private String eventTimeDateTime;
    boolean withDateTime;


    /**
     * Construct a TimestampedAtomLog with the timestamp and with the date time if the log has
     * one, without it otherwise.
     * @param log the entire log
     */
    public TimestampedAtomLog(String log) {
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
        this.processingTimeTimeStamp = Long.parseLong(logParts[TimestampedAtomLogIndexes.PROCESSING_TIME_TIMESTAMP.getIndex()]);
        this.eventTimeTimeStamp = Long.parseLong(logParts[TimestampedAtomLogIndexes.EVENT_TIME_TIMESTAMP.getIndex()]);

        withDateTime = dateTimePattern.matcher(logParts[TimestampedAtomLogIndexes.EVENT_TIME_DATETIME.getIndex()]).find();
        int startIndex = withDateTime
                ? TimestampedAtomLogIndexes.EVENT_TIME_DATETIME.getIndex() + 1
                : TimestampedAtomLogIndexes.EVENT_TIME_TIMESTAMP.getIndex() + 1;
        this.eventTimeDateTime = withDateTime
                ? logParts[TimestampedAtomLogIndexes.EVENT_TIME_DATETIME.getIndex()]
                : "";
        this.atomLog = AtomLogFactory.createAtomLog(Arrays.copyOfRange(logParts, startIndex, logParts.length));
    }


    public AtomLog getAtomLog() {
        return atomLog;
    }

    public long getProcessingTimeTimeStamp() {
        return processingTimeTimeStamp;
    }

    public long getEventTimeTimeStamp() {
        return eventTimeTimeStamp;
    }

    public String getEventTimeDateTime() {
        return eventTimeDateTime;
    }

    public boolean isWithDateTime() {
        return withDateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(processingTimeTimeStamp).append(AtomLogFactory.ATOM_LOG_SEPARATOR);
        sb.append(eventTimeTimeStamp).append(AtomLogFactory.ATOM_LOG_SEPARATOR);
        if (withDateTime)
            sb.append(eventTimeDateTime).append(AtomLogFactory.ATOM_LOG_SEPARATOR);
        sb.append(atomLog.toString());
        return sb.toString();
    }

    enum TimestampedAtomLogIndexes {
        PROCESSING_TIME_TIMESTAMP(0),
        EVENT_TIME_TIMESTAMP(1),
        EVENT_TIME_DATETIME(2);
        int index;
        TimestampedAtomLogIndexes(int i) {this.index = i;}
        public int getIndex() {return index;}
    }
}
