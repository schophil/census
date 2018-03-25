package lb.census.record.recorders;

import java.util.Date;
import java.util.HashMap;

import lb.census.record.metrics.MetricsCalculator;
import lb.census.record.metrics.SubKeyMetricsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lb.census.dao.DayStatsDao;
import lb.census.dao.UserStatsDao;
import lb.census.model.DayStats;
import lb.census.model.UserStats;
import lb.census.record.log.LogRecord;

/**
 * @author phili
 */
@Component
public class UserStatsRecorder implements Recorder {

    private static final Logger LOGGER = LoggerFactory.getLogger("CENSUS_RECORDERS");
    private SubKeyMetricsCollector<MetricsCalculator> metricsPerUser;
    @Autowired
    private DayStatsDao dayStatsDao;
    @Autowired
    private UserStatsDao userStatsDao;

    public DayStatsDao getDayStatsDao() {
        return dayStatsDao;
    }

    public void setDayStatsDao(DayStatsDao dayStatsDao) {
        this.dayStatsDao = dayStatsDao;
    }

    public UserStatsDao getUserStatsDao() {
        return userStatsDao;
    }

    public void setUserStatsDao(UserStatsDao userStatsDao) {
        this.userStatsDao = userStatsDao;
    }

    @Override
    public void initialize() {
        // Keep metrics per user id.
        metricsPerUser = new SubKeyMetricsCollector(lr -> lr.getUserId(), () -> new MetricsCalculator(2));
    }

    @Override
    public void record(LogRecord logRecord, RecorderContext recorderContext) {
        metricsPerUser.add(logRecord);
    }

    @Override
    public void store(Date date, RecorderContext recorderContext) {
        DayStats dayStats = recorderContext.getCurrentDayStats();
        if (dayStats == null) {
            throw new RuntimeException("Unable to find day stats for " + date);
        }

        HashMap<String, MetricsCalculator> metricsCollectors = metricsPerUser.getMetricsCollectors();

        // Update the stats per day with the total number of users.
        int totalUserIds = metricsCollectors.size();
        if (metricsCollectors.containsKey("-")) {
            totalUserIds--;
        }
        dayStats.setTotalUserIds(totalUserIds);
        dayStatsDao.save(dayStats);

        // Save the stats per user.
        metricsPerUser.getMetricsCollectors().entrySet().stream().forEach(entry -> {
            MetricsCalculator metricsCalculator = entry.getValue();
            String userId = entry.getKey();

            UserStats userStats = new UserStats();
            userStats.setDayStats(dayStats);
            userStats.setUserId(userId);

            metricsCalculator.update(userStats);

            userStatsDao.save(userStats);
        });

        metricsPerUser = null;
    }

    @Override
    public void forget(RecorderContext recorderContext) {
    }
}
