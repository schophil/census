package lb.census.rest.subjects.stats;

import com.fasterxml.jackson.annotation.JsonFormat;
import lb.census.model.DayStats;
import lb.census.model.DayStatsReport;
import lb.census.rest.shared.Metrics;

import java.util.Date;

/**
 * Created by phili on 21/12/2015.
 */
public class OneDay extends Metrics {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
    public Date date;
    public Integer totalUserIds;

    public static OneDay map(DayStats dayStats, OneDay oneDay) {
        if (dayStats == null) {
            throw new IllegalArgumentException("Day stats is null");
        }
        if (oneDay == null) {
            throw new IllegalArgumentException("One day is null");
        }
        oneDay.date = dayStats.getDate();
        oneDay.map(dayStats);
        oneDay.totalUserIds = dayStats.getTotalUserIds();
        return oneDay;
    }

    public static OneDay of(DayStats dayStats) {
        if (dayStats == null) {
            throw new IllegalArgumentException("Day stats is null");
        }
        return map(dayStats, new OneDay());
    }

    public static OneDay of(DayStatsReport dayStatsReport) {
        if (dayStatsReport == null) {
            throw new IllegalArgumentException("Day stats report is null");
        }
        OneDay oneDay = new OneDay();
        oneDay.date = dayStatsReport.getDate();
        oneDay.averageResponseTime = dayStatsReport.getAverageResponseTime();
        oneDay.totalRequests = dayStatsReport.getTotalRequests();
        oneDay.totalUserIds = dayStatsReport.getTotalUserIds();
        return oneDay;
    }
}
