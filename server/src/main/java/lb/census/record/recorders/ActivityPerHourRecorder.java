package lb.census.record.recorders;

import java.util.*;

import lb.census.math.AverageCalculator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lb.census.dao.DayStatsDao;
import lb.census.dao.TotalActivityPerHourDao;
import lb.census.dao.UserActivityPerHourDao;
import lb.census.model.DayStats;
import lb.census.model.TotalActivityPerHour;
import lb.census.model.UserActivityPerHour;
import lb.census.record.log.LogRecord;

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

    private final Calendar calendar = Calendar.getInstance();

    // global statistics per hour
    private final int[] globalHits = new int[24];
    private final AverageCalculator[] globalResponseTime = new AverageCalculator[24];

    // user statistics per hour
    private class User {
        String id = null;
        int[] hitsPerHour = new int[24];
        AverageCalculator[] responseTime = new AverageCalculator[24];
    }
    private final Map<String, User> userStats = new HashMap<>(100);
    //private final Map<String, User> userStats = new TreeMap<>();

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

    public ActivityPerHourRecorder() {
        resetHours(globalHits);
        calendar.clear();
        userStats.clear();
    }

    @Override
    public void initialize() {
    }

    @Override
    public void record(LogRecord logRecord, RecorderContext recorderContext) {
        calendar.setTime(logRecord.getTimestamp());
        int hourOfTheDay = calendar.get(Calendar.HOUR_OF_DAY);
        LOGGER.debug("Recording day activity on hour {} for timestamp {} and user {}", hourOfTheDay, logRecord.getTimestamp(), logRecord.getUserId());

        // Record the global activity per hour
        globalHits[hourOfTheDay]++;
        recordResponseTime(globalResponseTime, hourOfTheDay, logRecord.getResponseTime());

        // Record the activity per hour for a specific user
        String key = StringUtils.reverse(logRecord.getUserId());
        User user = userStats.get(key);
        if (user == null) {
            user = new User();
            user.id = logRecord.getUserId();
            userStats.put(key, user);
        }
        user.hitsPerHour[hourOfTheDay]++;
        recordResponseTime(user.responseTime, hourOfTheDay, logRecord.getResponseTime());
    }

    @Override
    public void store(Date date, RecorderContext recorderContext) {
        DayStats dayStats = recorderContext.getCurrentDayStats();
        if (dayStats == null) {
            throw new RuntimeException("Unable to find day stats for " + date);
        }
        storeGlobalStats(dayStats);
        storeUserStats(dayStats);
    }

    @Override
    public void forget(RecorderContext recorderContext) {
        resetHours(globalHits);
        userStats.clear();
    }

    private void storeGlobalStats(DayStats dayStats) {
        // If there are hits, there also need to be response time. So we can only iterate over the hits
        // and safely take the response time as well.
        for (int i = 0; i < globalHits.length; i++) {
            if (globalHits[i] != 0) {
                TotalActivityPerHour dayActivity = new TotalActivityPerHour();
                dayActivity.setDayStats(dayStats);
                dayActivity.setHour(i);
                dayActivity.setHits(globalHits[i]);

                if (globalResponseTime[i] != null) {
                    dayActivity.setAverageResponseTime(globalResponseTime[i].getCurrentAverage().doubleValue());
                } else {
                    LOGGER.error("In the global stats there are hits without response time for hour {}", i);
                }

                totalActivityPerHourDao.save(dayActivity);
            }
        }
    }

    private void storeUserStats(DayStats dayStats) {
        userStats.values().stream().forEach(user -> {
            // If there are hits, there also need to be response time. So we can only iterate over the hits
            // and safely take the response time as well.
            for (int i = 0; i < user.hitsPerHour.length; i++) {
                if (user.hitsPerHour[i] != 0) {
                    UserActivityPerHour userActivity = new UserActivityPerHour();
                    userActivity.setDayStats(dayStats);
                    userActivity.setHour(i);
                    userActivity.setHits(user.hitsPerHour[i]);
                    userActivity.setUserId(user.id);

                    if (user.responseTime[i] != null) {
                        userActivity.setAverageResponseTime(user.responseTime[i].getCurrentAverage().doubleValue());
                    } else {
                        LOGGER.error("In the user stats there are hits without response time for user {} and hour {}", user.id, i);
                    }

                    userActivityPerHourDao.save(userActivity);
                }
            }
        });
    }

    private void recordResponseTime(AverageCalculator[] recorded, int position, double newValue) {
        if (recorded[position] == null) {
            recorded[position] = AverageCalculator.create(2);
        }
        recorded[position].add(newValue);
    }

    private void resetHours(int[] hours) {
        for (int i = 0; i < hours.length; i++) {
            hours[i] = 0;
        }
    }
}
