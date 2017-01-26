package lb.census.record.recorders;

import java.util.Date;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lb.census.dao.DayStatsDao;
import lb.census.dao.UserStatsDao;
import lb.census.math.OccurrenceCounter;
import lb.census.model.DayStats;
import lb.census.model.UserStats;
import lb.census.record.log.LogRecord;

/**
 * 
 * @author phili
 */
@Component
public class UserStatsRecorder implements Recorder {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserStatsRecorder.class);
    private final OccurrenceCounter<String, String> occurrenceCounter = new OccurrenceCounter<>();
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
    }

    @Override
    public void record(LogRecord logRecord, RecorderContext recorderContext) {
        String resultCode = logRecord.getResultCode();
        if ("200".equals(resultCode)) {
            resultCode = "ok";
        } else {
            resultCode = "nok";
        }

        // collect result per user id
        occurrenceCounter.register(logRecord.getUserId(), resultCode);
        occurrenceCounter.register(logRecord.getUserId(), "total");
    }

    @Override
    public void store(Date date, RecorderContext recorderContext) {
        // create the user specific entries
        occurrenceCounter.getGroups().stream().forEach(userId -> {
            LOGGER.debug("Create user stats for user {}", userId);
            Integer totalRequests = occurrenceCounter.getOccurrences(userId, "total");
            Integer totalRequestsInError = occurrenceCounter.getOccurrences(userId, "nok");

            DayStats dayStats = recorderContext.getCurrentDayStats();
            if (dayStats == null) {
                throw new RuntimeException("Unable to find day stats for " + date);
            }

            Set<String> recordedUserIds = occurrenceCounter.getGroups();
            int totalUserIds = recordedUserIds.size();
            if (recordedUserIds.contains("-")) {
                totalUserIds--;
            }
            dayStats.setTotalUserIds(totalUserIds);
            dayStatsDao.save(dayStats);

            UserStats userStats = new UserStats();
            userStats.setDayStats(dayStats);
            userStats.setUserId(userId);
            userStats.setTotalRequests(totalRequests);
            userStats.setTotalRequestsInError(totalRequestsInError);
            userStatsDao.save(userStats);            
        });
    }

    @Override
    public void forget(RecorderContext recorderContext) {
        occurrenceCounter.clear();
    }
}
