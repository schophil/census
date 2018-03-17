package lb.census.record.metrics;

import lb.census.record.log.LogRecord;

/**
 * Simple counter metrics that counts the number of log record that it received.
 */
public class CounterMetrics implements MetricsCollector {

    private int counter;

    @Override
    public void add(LogRecord logRecord) {
        counter++;
    }

    public int getCounter() {
        return counter;
    }
}
