package lb.census.rest.subjects.stats;

import lb.census.dao.*;
import lb.census.model.CategoryFilter;
import lb.census.rest.shared.Period;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by philippeschottey on 03/03/2017.
 */
public class StatsPerDayService {

    @Autowired
    private DayStatsDao dayStatsDao;
    @Autowired
    private TotalActivityPerHourDao totalActivityPerHourDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ResourceDao resourceDao;
    @Autowired
    private UserActivityPerHourDao userActivityPerHourDao;

    public List<OneDay> list(Period period, List<CategoryFilter> categoryFilters) {
        return null;
    }

    /**
     * Without catgeory filter we can just use the daily statistics. Only the period needs to be applied.
     *
     * @param period
     * @return
     */
    public List<OneDay> list(Period period) {
        return null;
    }
}
