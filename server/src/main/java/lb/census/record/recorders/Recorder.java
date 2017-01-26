package lb.census.record.recorders;

import java.util.Date;

import lb.census.record.log.LogRecord;

public interface Recorder {

    void initialize();

    /**
     * Record one line.
     *
     * @param logRecord
     * @param recorderContext
     */
    void record(LogRecord logRecord, RecorderContext recorderContext);

    /**
     * Indicates all the lines were recorder and the data can be stored.
     *
     * @param date The date for which the stats are recorded
     * @param recorderContext
     */
    void store(Date date, RecorderContext recorderContext);

    /**
     * Clear the data without storing the result.
     *
     * @param recorderContext
     */
    void forget(RecorderContext recorderContext);
}
