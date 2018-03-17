package lb.census.record.metrics;

import lb.census.record.log.LogRecord;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Subdivices metrics per hour of the day. The hour of the day is deduced from the {@link LogRecord} instances that
 * are being processed.
 *
 * As more log records are collected; more sub-metrics are created. To create sub-metrics this class needs
 * a {@link MetricsCollectorCreator}.
 *
 * @param <T> The metrics collector type to keep per key
 */
public class SubHourMetricsCollector<T extends MetricsCollector> implements MetricsCollector {

    private final MetricsCollectorCreator<T> metricsCollectorCreator;
    private final Object[] metricsCollectors;
    private final Calendar calendar;

    public SubHourMetricsCollector(MetricsCollectorCreator<T> metricsCollectorCreator) {
        this.metricsCollectorCreator = metricsCollectorCreator;
        this.metricsCollectors = new Object[24];
        this.calendar = Calendar.getInstance();
    }

    @Override
    public void add(LogRecord logRecord) {
        int houtOfTheDay = getHourOfTheDay(logRecord);
        T metricsCollector = (T) metricsCollectors[houtOfTheDay];
        if (metricsCollector == null) {
            metricsCollector = metricsCollectorCreator.create();
            metricsCollectors[houtOfTheDay] = metricsCollector;
        }
        metricsCollector.add(logRecord);
    }

    public T[] getMetricsCollectors(T[] array) {
        System.arraycopy(metricsCollectors, 0, array, 0, metricsCollectors.length);
        return array;
    }

    private int getHourOfTheDay(LogRecord lr) {
        calendar.setTime(lr.getTimestamp());
        return calendar.get(Calendar.HOUR_OF_DAY);
    }
}
