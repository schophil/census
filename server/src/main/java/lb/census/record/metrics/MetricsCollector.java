package lb.census.record.metrics;

import lb.census.record.log.LogRecord;

public interface MetricsCollector {

    void add(LogRecord logRecord);
}
