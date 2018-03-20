package lb.census.rest.shared;

import lb.census.model.DefaultMetrics;

public class Metrics {

    public Integer totalRequests = 0;
    public Integer totalRequestsInError = 0;
    public Double averageResponseTime = 0.0;
    public Double maxResponseTime = 0.0;
    public Double minResponseTime = 0.0;

    public void map(DefaultMetrics defaultMetrics) {
        if (defaultMetrics != null) {
            this.totalRequests = defaultMetrics.getTotalRequests();
            this.totalRequestsInError = defaultMetrics.getTotalRequestsInError();
            this.averageResponseTime = defaultMetrics.getAverageResponseTime();
            this.maxResponseTime = defaultMetrics.getMaxResponseTime();
            this.minResponseTime = defaultMetrics.getMinResponseTime();
        }
    }
}
