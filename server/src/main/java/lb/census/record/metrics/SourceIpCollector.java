package lb.census.record.metrics;

import lb.census.record.log.LogRecord;

import java.util.HashSet;

/**
 *
 */
public class SourceIpCollector implements  MetricsCollector {

    private HashSet<String> ips = new HashSet<>();

    @Override
    public void add(LogRecord logRecord) {
        ips.add(logRecord.getSourceIp());
    }

    public HashSet<String> getIps() {
        return ips;
    }
}
