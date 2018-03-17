package lb.census.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class DefaultMetrics {

    @Column(name = "rtotal")
    private Integer totalRequests = 0;
    @Column(name = "rerror")
    private Integer totalRequestsInError = 0;
    @Column(name = "raverage")
    private Double averageResponseTime = 0.0;
    @Column(name = "rmax")
    private Double maxResponseTime = 0.0;
    @Column(name = "rmin")
    private Double minResponseTime = 0.0;

    public Integer getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Integer totalRequests) {
        this.totalRequests = totalRequests;
    }

    public Integer getTotalRequestsInError() {
        return totalRequestsInError;
    }

    public void setTotalRequestsInError(Integer totalRequestsInError) {
        this.totalRequestsInError = totalRequestsInError;
    }

    public Double getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(Double averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public Double getMaxResponseTime() {
        return maxResponseTime;
    }

    public void setMaxResponseTime(Double maxResponseTime) {
        this.maxResponseTime = maxResponseTime;
    }

    public Double getMinResponseTime() {
        return minResponseTime;
    }

    public void setMinResponseTime(Double minResponseTime) {
        this.minResponseTime = minResponseTime;
    }
}
