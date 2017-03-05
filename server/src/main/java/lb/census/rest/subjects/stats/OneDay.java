package lb.census.rest.subjects.stats;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lb.census.dao.ReportDao;
import lb.census.model.DayStats;
import lb.census.model.DayStatsReport;

/**
 * Created by phili on 21/12/2015.
 */
public class OneDay {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
    public Date date;
    public Integer totalRequests;
    public Integer totalRequestsInError;
    public Double averageResponseTime;
    public Double maxResponseTime;
    public Double minResponseTime;
    public Integer totalUserIds;
    public Integer share;

    public static OneDay map(DayStats dayStats, OneDay oneDay) {
        oneDay.date = dayStats.getDate();
        oneDay.averageResponseTime = dayStats.getAverageResponseTime();
        oneDay.maxResponseTime = dayStats.getMaxResponseTime();
        oneDay.minResponseTime = dayStats.getMinResponseTime();
        oneDay.totalRequests = dayStats.getTotalRequests();
        oneDay.totalRequestsInError = dayStats.getTotalRequestsInError();
        oneDay.totalUserIds = dayStats.getTotalUserIds();
        return oneDay;
    }

    public static OneDay from(DayStats dayStats) {
        return map(dayStats, new OneDay());
    }

    public static OneDay from(DayStatsReport dayStatsReport) {
        OneDay oneDay = new OneDay();
        oneDay.date = dayStatsReport.getDate();
        oneDay.averageResponseTime = dayStatsReport.getAverageResponseTime();
        oneDay.totalRequests = dayStatsReport.getTotalRequests();
        return oneDay;
    }
}
