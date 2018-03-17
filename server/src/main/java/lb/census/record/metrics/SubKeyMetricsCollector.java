package lb.census.record.metrics;

import lb.census.record.log.LogRecord;

import java.util.HashMap;

/**
 * Subdivides metrics per key. The key is calculated based on the attributes of a {@link LogRecord} and can be anything.
 *
 * As more log records are collected; more sub-metrics are created. To create sub-metrics this class needs
 * a {@link MetricsCollectorCreator}.
 *
 * The keys are constructed using a {@link KeyCreator}.
 *
 * The code example below creates an instances that keeps metrics per user.
 * <pre>
 *     new SubKeyMetricsCollector<MetricsCalculator>(
 *      logRecord -> logRecord.getUserId(),
 *      () -> new MetricsCalculator(2)
 *     );
 * </pre>
 *
 * @param <T> The metrics collector type to keep per key
 */
public class SubKeyMetricsCollector<T extends MetricsCollector> implements MetricsCollector {

    private final HashMap<String, T> metricsCollectors = new HashMap<>();
    private final KeyCreator keyCreator;
    private final MetricsCollectorCreator<T> metricsCollectorCreator;

    public SubKeyMetricsCollector(KeyCreator keyCreator, MetricsCollectorCreator<T> metricsCollectorCreator) {
        this.keyCreator = keyCreator;
        this.metricsCollectorCreator = metricsCollectorCreator;
    }

    public void add(LogRecord logRecord) {
        String key = keyCreator.create(logRecord);

        if (metricsCollectors.containsKey(key)) {
            metricsCollectors.get(key).add(logRecord);
        } else {
            T metricsCollector = metricsCollectorCreator.create();
            metricsCollector.add(logRecord);
            metricsCollectors.put(key, metricsCollector);
        }
    }

    public HashMap<String, T> getMetricsCollectors() {
        return metricsCollectors;
    }
}
