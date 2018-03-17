package lb.census.record.metrics;

import lb.census.record.log.LogRecord;

/**
 * Interface of a metrics collector. A metrics collector must be able to process {@link LogRecord} instances.
 */
public interface MetricsCollector {

    /**
     * Process one log record
     * @param logRecord
     */
    void add(LogRecord logRecord);
}
