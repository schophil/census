package lb.census.record.metrics;

import lb.census.record.log.LogRecord;

@FunctionalInterface
public interface KeyCreator {
    String create(LogRecord logRecord);
}
