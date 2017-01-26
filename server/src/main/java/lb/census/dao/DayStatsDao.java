package lb.census.dao;

import java.util.Date;
import java.util.List;

import lb.census.model.DayStats;

public interface DayStatsDao {

    void save(DayStats dayStats);

    DayStats getDayStats(Date date, String subject);

    List<DayStats> list(Date from, Date until, String subject);

    void delete(DayStats dayStats);
}
