package lb.census.dao;

import lb.census.model.DayStatsReport;

import java.util.Date;
import java.util.List;

/**
 * Created by philippeschottey on 12/02/2017.
 */
public interface ReportDao {

    /**
     * Calculates daily stats only taking into account users that match the filter.
     *
     * @param from               The beginning of the period to analyse
     * @param until              The end of the period to analyse
     * @param subject            The subject to apply on (extra filter on users)
     * @param userCategoryFilter A filter expression on the users
     */
    List<DayStatsReport> listDailyStats(Date from, Date until, String subject, String userCategoryFilter);
}
