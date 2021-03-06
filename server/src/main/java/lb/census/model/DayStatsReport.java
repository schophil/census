package lb.census.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import java.util.Date;

/**
 * Created by philippeschottey on 14/02/2017.
 */
public class DayStatsReport {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
    private Date date;
    private Integer totalRequests;
    private Double averageResponseTime;
    private Integer totalUserIds;

    public Integer getTotalUserIds() {
        return totalUserIds;
    }

    public void setTotalUserIds(Integer totalUserIds) {
        this.totalUserIds = totalUserIds;
    }

    public Double getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(Double averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Integer totalRequests) {
        this.totalRequests = totalRequests;
    }
}
