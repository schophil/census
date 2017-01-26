package lb.census.record.recorders;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
 *
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
    private final int[] globalHits = new int[24];
    private final Calendar calendar = Calendar.getInstance();
    private final Map<String, int[]> hitsPerUser = new HashMap<>();

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
        recordGlobalHit(hourOfTheDay);
        recordHitForUser(hourOfTheDay, logRecord.getUserId());
    }

    @Override
    public void store(Date date, RecorderContext recorderContext) {
        DayStats dayStats = recorderContext.getCurrentDayStats();
        if (dayStats == null) {
            throw new RuntimeException("Unable to find day stats for " + date);
        }
        storeGlobalHits(dayStats);
        storeUserHits(dayStats);
    }

    @Override
    public void forget(RecorderContext recorderContext) {
        resetHours(globalHits);
        hitsPerUser.clear();
    }

    private void storeGlobalHits(DayStats dayStats) {
        for (int i = 0; i < globalHits.length; i++) {
            if (globalHits[i] != 0) {
                TotalActivityPerHour dayActivity = new TotalActivityPerHour();
                dayActivity.setDayStats(dayStats);
                dayActivity.setHour(i);
                dayActivity.setHits(globalHits[i]);

                totalActivityPerHourDao.save(dayActivity);
            }
        }
    }

    private void storeUserHits(DayStats dayStats) {
        hitsPerUser.keySet().stream().forEach((userId) -> {
            int[] hitsPerHour = hitsPerUser.get(userId);
            for (int i = 0; i < hitsPerHour.length; i++) {
                if (hitsPerHour[i] != 0) {
                    UserActivityPerHour userActivity = new UserActivityPerHour();
                    userActivity.setDayStats(dayStats);
                    userActivity.setHour(i);
                    userActivity.setHits(hitsPerHour[i]);
                    userActivity.setUserId(userId);

                    userActivityPerHourDao.save(userActivity);
                }
            }
        });
    }

    private void recordGlobalHit(int hourOfTheDay) {
        globalHits[hourOfTheDay]++;
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

    private void resetHours(int[] hours) {
        for (int i = 0; i < hours.length; i++) {
            hours[i] = 0;
        }
    }
}
