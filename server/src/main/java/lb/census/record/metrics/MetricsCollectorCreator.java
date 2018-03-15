package lb.census.record.metrics;

@FunctionalInterface
public interface MetricsCollectorCreator<T extends MetricsCollector> {
    T create();
}
