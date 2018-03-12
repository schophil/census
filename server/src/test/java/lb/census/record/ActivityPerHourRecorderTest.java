package lb.census.record;

import lb.census.dao.DayStatsDao;
import lb.census.dao.TotalActivityPerHourDao;
import lb.census.dao.UserActivityPerHourDao;
import lb.census.model.DayStats;
import lb.census.model.Subject;
import lb.census.model.TotalActivityPerHour;
import lb.census.record.log.LogRecord;
import lb.census.record.recorders.ActivityPerHourRecorder;
import lb.census.record.recorders.RecorderContext;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by philippeschottey on 16/02/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
        "census.data=file:///tmp",
        "census.config=file:///config",
        "logging.level.root=ERROR",
        "logging.level.lb.census=TRACE",
        "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.format_sql=true"
})
@Transactional
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
        logRecord.setTimestamp(DateUtils.setHours(new Date(), 3));
        records.add(logRecord);

        logRecord = new LogRecord();
        logRecord.setUserId("user2");
        logRecord.setResponseTime(0.03);
        logRecord.setTimestamp(DateUtils.setHours(new Date(), 3));
        records.add(logRecord);

        logRecord = new LogRecord();
        logRecord.setUserId("user3");
        logRecord.setResponseTime(0.02);
        logRecord.setTimestamp(DateUtils.setHours(new Date(), 4));
        records.add(logRecord);
    }

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
        assertThat(activityPerHours.size(), is(2));
    }
}
