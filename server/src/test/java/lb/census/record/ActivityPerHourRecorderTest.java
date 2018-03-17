package lb.census.record;

import lb.census.CommonTestsConfiguration;
import lb.census.dao.DayStatsDao;
import lb.census.dao.TotalActivityPerHourDao;
import lb.census.dao.UserActivityPerHourDao;
import lb.census.model.DayStats;
import lb.census.model.Subject;
import lb.census.model.TotalActivityPerHour;
import lb.census.model.UserActivityPerHour;
import lb.census.record.log.LogRecord;
import lb.census.record.recorders.ActivityPerHourRecorder;
import lb.census.record.recorders.RecorderContext;
import org.apache.commons.lang3.time.DateUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by philippeschottey on 16/02/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(CommonTestsConfiguration.class)
public class ActivityPerHourRecorderTest {

    @Autowired
    private ActivityPerHourRecorder recorder;
    @Autowired
    private DayStatsDao dayStatsDao;
    @Autowired
    private TotalActivityPerHourDao totalActivityPerHourDao;
    @Autowired
    private UserActivityPerHourDao userActivityPerHourDao;
    private List<LogRecord> records;

    @Before
    public void initializeRecords() {
        records = new ArrayList<>();

        LogRecord logRecord = new LogRecord();
        logRecord.setUserId("user1");
        logRecord.setResponseTime(0.02);
        logRecord.setResultCode("200");
        logRecord.setTimestamp(DateUtils.setHours(new Date(), 3));
        records.add(logRecord);

        logRecord = new LogRecord();
        logRecord.setUserId("user2");
        logRecord.setResponseTime(0.03);
        logRecord.setResultCode("200");
        logRecord.setTimestamp(DateUtils.setHours(new Date(), 3));
        records.add(logRecord);

        logRecord = new LogRecord();
        logRecord.setUserId("user3");
        logRecord.setResponseTime(0.02);
        logRecord.setResultCode("500");
        logRecord.setTimestamp(DateUtils.setHours(new Date(), 4));
        records.add(logRecord);
    }

    @Transactional
    @Test
    public void recordAndRetrieve() {
        DayStats dayStats = new DayStats();
        dayStats.setDate(new Date());
        dayStatsDao.save(dayStats);

        RecorderContext recorderContext = new RecorderContext(new Subject("1", "testsuite"));
        recorderContext.setCurrentDayStats(dayStats);

        recorder.initialize();
        for (LogRecord logRecord : records) {
            recorder.record(logRecord, recorderContext);
        }
        recorder.store(new Date(), recorderContext);

        recorderContext.getCurrentDayStats();

        List<TotalActivityPerHour> activityPerHours = totalActivityPerHourDao.getDayActivity(dayStats);
        assertThat(activityPerHours).isNotNull();
        assertThat(activityPerHours).isNotEmpty();
        assertThat(activityPerHours).doesNotContainNull();
        assertThat(activityPerHours.size()).isEqualTo(24);

        // 3:00 - should contain metrics for 2 requests
        assertThat(activityPerHours.get(3)).isNotNull();
        assertThat(activityPerHours.get(3).getHour()).isEqualTo(3);
        assertThat(activityPerHours.get(3).getTotalRequests()).isEqualTo(2);
        assertThat(activityPerHours.get(3).getMaxResponseTime()).isEqualTo(0.03);
        assertThat(activityPerHours.get(3).getMinResponseTime()).isEqualTo(0.02);
        assertThat(activityPerHours.get(3).getAverageResponseTime()).isEqualTo(0.03);
        assertThat(activityPerHours.get(3).getTotalRequestsInError()).isZero();

        // 4:00 - should contain metrics for 1 request
        assertThat(activityPerHours.get(4)).isNotNull();
        assertThat(activityPerHours.get(4).getHour()).isEqualTo(4);
        assertThat(activityPerHours.get(4).getTotalRequests()).isEqualTo(1);
        assertThat(activityPerHours.get(4).getMaxResponseTime()).isEqualTo(0.02);
        assertThat(activityPerHours.get(4).getMinResponseTime()).isEqualTo(0.02);
        assertThat(activityPerHours.get(4).getAverageResponseTime()).isEqualTo(0.02);
        assertThat(activityPerHours.get(4).getTotalRequestsInError()).isOne();

        List<UserActivityPerHour> userActivityPerHours = userActivityPerHourDao.getActivities(dayStats, "user2");
        assertThat(userActivityPerHours).isNotNull();
        assertThat(userActivityPerHours).isNotEmpty();
        assertThat(userActivityPerHours).doesNotContainNull();
        assertThat(userActivityPerHours.size()).isEqualTo(24);

        // 1:00 - should be empty
        assertThat(userActivityPerHours.get(1).getUserId()).isEqualTo("user2");
        assertThat(userActivityPerHours.get(1).getTotalRequests()).isZero();
        assertThat(userActivityPerHours.get(1).getTotalRequestsInError()).isZero();
        assertThat(userActivityPerHours.get(1).getMaxResponseTime()).isZero();
        assertThat(userActivityPerHours.get(1).getMinResponseTime()).isZero();
        assertThat(userActivityPerHours.get(1).getAverageResponseTime()).isZero();

        // 3:00 - should contain metrics for 1 request
        assertThat(userActivityPerHours.get(3).getUserId()).isEqualTo("user2");
        assertThat(userActivityPerHours.get(3).getTotalRequests()).isOne();
        assertThat(userActivityPerHours.get(3).getTotalRequestsInError()).isZero();
        assertThat(userActivityPerHours.get(3).getMaxResponseTime()).isEqualTo(0.03);
        assertThat(userActivityPerHours.get(3).getMinResponseTime()).isEqualTo(0.03);
        assertThat(userActivityPerHours.get(3).getAverageResponseTime()).isEqualTo(0.03);
    }
}
