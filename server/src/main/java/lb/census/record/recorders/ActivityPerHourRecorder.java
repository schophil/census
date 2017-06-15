package lb.census.record.recorders;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lb.census.math.AverageCalculator;
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
    private final Map<String, int[]> hitsPerUser = new HashMap<>();
    private final Map<String, AverageCalculator[]> responseTimePerUser = new HashMap<>();

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
        hitsPerUser.clear();
    }

    @Override
    public void initialize() {
    }

    @Override
    public void record(LogRecord logRecord, RecorderContext recorderContext) {
        calendar.setTime(logRecord.getTimestamp());
        int hourOfTheDay = calendar.get(Calendar.HOUR_OF_DAY);
        LOGGER.debug("Recording day activity on hour {} for timestamp {}", hourOfTheDay, logRecord.getTimestamp());

        // Record the global activity per hour
        recordGlobalHit(hourOfTheDay);
        recordGlobalResponseTime(hourOfTheDay, logRecord.getResponseTime());

        // Record the activity per hour for a specific user
        recordHitForUser(hourOfTheDay, logRecord.getUserId());
        recordResponseTimeForUser(hourOfTheDay, logRecord.getUserId(), logRecord.getResponseTime());
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
        hitsPerUser.clear();
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
        hitsPerUser.keySet().stream().forEach((userId) -> {
            AverageCalculator[] responseTimePerHour = responseTimePerUser.get(userId);
            int[] hitsPerHour = hitsPerUser.get(userId);
            // If there are hits, there also need to be response time. So we can only iterate over the hits
            // and safely take the response time as well.
            for (int i = 0; i < hitsPerHour.length; i++) {
                if (hitsPerHour[i] != 0) {
                    UserActivityPerHour userActivity = new UserActivityPerHour();
                    userActivity.setDayStats(dayStats);
                    userActivity.setHour(i);
                    userActivity.setHits(hitsPerHour[i]);
                    userActivity.setUserId(userId);

                    if (responseTimePerHour[i] != null) {
                        userActivity.setAverageResponseTime(responseTimePerHour[i].getCurrentAverage().doubleValue());
                    } else {
                        LOGGER.error("In the user stats there are hits without response time for user {} and hour {}", userId, i);
                    }

                    userActivityPerHourDao.save(userActivity);
                }
            }
        });
    }

    private void recordGlobalHit(int hourOfTheDay) {
        globalHits[hourOfTheDay]++;
    }

    private void recordGlobalResponseTime(int hourOfTheDay, double responseTime) {
        recordResponseTime(globalResponseTime, hourOfTheDay, responseTime);
    }

    private void recordResponseTime(AverageCalculator[] recorded, int position, double newValue) {
        if (recorded[position] == null) {
            recorded[position] = AverageCalculator.create(2);
        }
        recorded[position].add(newValue);
    }

    private void recordHitForUser(int hourOfTheDay, String userId) {
        int[] hitsPerHour = hitsPerUser.get(userId);
        if (hitsPerHour == null) {
            hitsPerHour = new int[24];
            resetHours(hitsPerHour);
            hitsPerUser.put(userId, hitsPerHour);
        }
        hitsPerHour[hourOfTheDay]++;
    }

    private void recordResponseTimeForUser(int hourOfTheDay, String userId, double responseTime) {
        AverageCalculator[] userData = responseTimePerUser.get(userId);
        if (userData == null) {
            userData = new AverageCalculator[24];
            responseTimePerUser.put(userId, userData);
        }
        recordResponseTime(userData, hourOfTheDay, responseTime);
    }

    private void resetHours(int[] hours) {
        for (int i = 0; i < hours.length; i++) {
            hours[i] = 0;
        }
    }
}
