package lb.census.record.metrics;

import lb.census.record.log.LogRecord;

import java.util.Calendar;

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

    public T[] getMetricsCollectors() {
        return (T[]) metricsCollectors;
    }

    private int getHourOfTheDay(LogRecord lr) {
        calendar.setTime(lr.getTimestamp());
        return calendar.get(Calendar.HOUR_OF_DAY);
    }
}
