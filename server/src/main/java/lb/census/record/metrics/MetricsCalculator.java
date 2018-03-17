package lb.census.record.metrics;

import lb.census.math.AverageCalculator;
import lb.census.math.OccurrenceCounter;
import lb.census.model.DefaultMetrics;
import lb.census.record.log.LogRecord;

/**
 * Collects the following metrics:
 * <ul>
 *     <li>total requests</li>
 *     <li>total requests in error (not 200)</li>
 *     <li>max response time</li>
 *     <li>min response time</li>
 *     <li>average response time</li>
 * </ul>
 *
 * These metrics are taken from {@link LogRecord} instances.
 */
public class MetricsCalculator implements MetricsCollector {

    private static final String OK = "ok";
    private static final String NOK = "nok";
    private static final String CODE_200 = "200";

    private final AverageCalculator averageCalculator;
    private final OccurrenceCounter<String, String> occurrenceCounter;
    private double maxResponseTime = 0.0;
    private double minResponseTime = 0.0;

    public MetricsCalculator(int averageScale) {
        averageCalculator = AverageCalculator.create(averageScale);
        occurrenceCounter = new OccurrenceCounter<>(getClass().getName());
    }

    public void add(LogRecord logRecord) {
        averageCalculator.add(logRecord.getResponseTime());
        if (maxResponseTime < logRecord.getResponseTime()) {
            maxResponseTime = logRecord.getResponseTime();
        } else if (minResponseTime == 0.0
                || minResponseTime > logRecord.getResponseTime()) {
            minResponseTime = logRecord.getResponseTime();
        }

        occurrenceCounter.register(CODE_200.equals(logRecord.getResultCode()) ? OK : NOK);
    }

    public double getAverageResponseTime() {
        return averageCalculator.getCurrentAverage().doubleValue();
    }

    public double getMaxResponseTime() {
        return maxResponseTime;
    }

    public double getMinResponseTime() {
        return minResponseTime;
    }

    public int getTotalRequests() {
        return occurrenceCounter.getOccurrences(OK) + occurrenceCounter.getOccurrences(NOK);
    }

    public int getTotalRequestsInError() {
        return occurrenceCounter.getOccurrences(NOK);
    }

    public void update(DefaultMetrics defaultMetrics) {
        defaultMetrics.setTotalRequests(getTotalRequests());
        defaultMetrics.setTotalRequestsInError(getTotalRequestsInError());
        defaultMetrics.setAverageResponseTime(getAverageResponseTime());
        defaultMetrics.setMaxResponseTime(getMaxResponseTime());
        defaultMetrics.setMinResponseTime(getMinResponseTime());
    }
}
