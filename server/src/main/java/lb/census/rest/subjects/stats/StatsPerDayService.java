package lb.census.rest.subjects.stats;

import lb.census.dao.DayStatsDao;
import lb.census.dao.ReportDao;
import lb.census.model.CategoryFilter;
import lb.census.rest.shared.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by philippeschottey on 03/03/2017.
 */
@Service
public class StatsPerDayService {

    @Autowired
    private DayStatsDao dayStatsDao;

    @Autowired
    private ReportDao reportDao;

    /**
     * Without category filter we can just use the daily statistics. Only the period needs to be applied.
     *
     * @param period
     * @return
     */
    public List<OneDay> list(Period period, String subject) {
        assert period != null;
        assert subject != null;

        return dayStatsDao.list(period.getFrom(), period.getTo(), subject)
                .stream()
                .map(OneDay::from)
                .collect(Collectors.toList());
    }

    /**
     * @param period
     * @param categoryFilters
     * @param subject
     * @return
     */
    public List<OneDay> list(Period period, List<CategoryFilter> categoryFilters, String subject) {
        assert period != null;
        assert subject != null;

        return reportDao.listDailyStats(period.getFrom(), period.getTo(), subject, categoryFilters)
                .stream()
                .map(OneDay::from)
                .collect(Collectors.toList());
    }
}
