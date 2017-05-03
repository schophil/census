package lb.census.dao;

import java.util.Date;
import java.util.List;

import lb.census.model.DayStats;
import lb.census.model.TotalActivityPerHour;

/**
 * @author psc
 */
public interface TotalActivityPerHourDao {

    void save(TotalActivityPerHour dayActivity);

    List<TotalActivityPerHour> getDayActivity(Date date);

    List<TotalActivityPerHour> getDayActivity(DayStats dayStats);
}
