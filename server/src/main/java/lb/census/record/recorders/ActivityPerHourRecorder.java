package lb.census.record.recorders;

import lb.census.dao.DayStatsDao;
import lb.census.dao.TotalActivityPerHourDao;
import lb.census.dao.UserActivityPerHourDao;
import lb.census.model.DayStats;
import lb.census.model.TotalActivityPerHour;
import lb.census.model.UserActivityPerHour;
import lb.census.record.log.LogRecord;
import lb.census.record.metrics.MetricsCalculator;
import lb.census.record.metrics.SubHourMetricsCollector;
import lb.census.record.metrics.SubKeyMetricsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Records the activity per hour: global and per user.
 */
@Component
public class ActivityPerHourRecorder implements Recorder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityPerHourRecorder.class);

    @Autowired
    private DayStatsDao dayStatsDao;
    @Autowired
    private TotalActivityPerHourDao totalActivityPerHourDao;
    @Autowired
    private UserActivityPerHourDao userActivityPerHourDao;

    private SubHourMetricsCollector<MetricsCalculator> metricsPerHour;
    private SubKeyMetricsCollector<SubHourMetricsCollector<MetricsCalculator>> metricsPerUserAndHour;

    public DayStatsDao getDayStatsDao() {
        return dayStatsDao;
    }

    public void setDayStatsDao(DayStatsDao dayStatsDao) {
        this.dayStatsDao = dayStatsDao;
    }

    public TotalActivityPerHourDao getTotalActivityPerHourDao() {
        return totalActivityPerHourDao;
    }

    public void setTotalActivityPerHourDao(TotalActivityPerHourDao totalActivityPerHourDao) {
        this.totalActivityPerHourDao = totalActivityPerHourDao;
    }

    public UserActivityPerHourDao getUserActivityPerHourDao() {
        return userActivityPerHourDao;
    }

    public void setUserActivityPerHourDao(UserActivityPerHourDao userActivityPerHourDao) {
        this.userActivityPerHourDao = userActivityPerHourDao;
    }

    @Override
    public void initialize() {
        metricsPerHour = makeMetricsPerHour();
        metricsPerUserAndHour = new SubKeyMetricsCollector<>(lr -> lr.getUserId(),
                ActivityPerHourRecorder::makeMetricsPerHour);
    }

    @Override
    public void record(LogRecord logRecord, RecorderContext recorderContext) {
        metricsPerHour.add(logRecord);
        metricsPerUserAndHour.add(logRecord);
    }

    @Override
    public void store(Date date, RecorderContext recorderContext) {
        DayStats dayStats = recorderContext.getCurrentDayStats();

        storeGlobalStats(dayStats);
        metricsPerHour = null; // clear memory immediately

        storeUserStats(dayStats);
        metricsPerUserAndHour = null; // clear memory immediately
    }

    @Override
    public void forget(RecorderContext recorderContext) {
    }

    private void storeGlobalStats(DayStats dayStats) {
        MetricsCalculator[] metricsCollectors = metricsPerHour.getMetricsCollectors(new MetricsCalculator[24]);
        for (int i = 0; i < 24; i++) {
            MetricsCalculator metricsCalculator = metricsCollectors[i];

            TotalActivityPerHour dayActivity = new TotalActivityPerHour();
            dayActivity.setDayStats(dayStats);
            dayActivity.setHour(i);

            if (metricsCalculator != null) {
                metricsCalculator.update(dayActivity);
            }

            totalActivityPerHourDao.save(dayActivity);
        }
    }

    private void storeUserStats(DayStats dayStats) {
        metricsPerUserAndHour.getMetricsCollectors().entrySet().stream().forEach(entry -> {
            String userId = entry.getKey();
            SubHourMetricsCollector<MetricsCalculator> metricsCollector = entry.getValue();

            MetricsCalculator[] metricsCollectors = metricsCollector.getMetricsCollectors(new MetricsCalculator[24]);
            for (int i = 0; i < 24; i++) {
                MetricsCalculator metricsCalculator = metricsCollectors[i];

                UserActivityPerHour userActivity = new UserActivityPerHour();
                userActivity.setDayStats(dayStats);
                userActivity.setHour(i);
                userActivity.setUserId(userId);

                if (metricsCalculator != null) {
                    metricsCalculator.update(userActivity);
                }

                userActivityPerHourDao.save(userActivity);
            }

        });
    }

    private static SubHourMetricsCollector<MetricsCalculator> makeMetricsPerHour() {
        return new SubHourMetricsCollector<>(() -> new MetricsCalculator(2));
    }
}
