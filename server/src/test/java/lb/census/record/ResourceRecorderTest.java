package lb.census.record;

import lb.census.CommonTestsConfiguration;
import lb.census.dao.DayStatsDao;
import lb.census.dao.ResourceDao;
import lb.census.model.DayStats;
import lb.census.model.Resource;
import lb.census.model.Subject;
import lb.census.record.log.LogRecord;
import lb.census.record.recorders.RecorderContext;
import lb.census.record.recorders.ResourceRecorder;
import org.apache.commons.lang3.time.DateUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(CommonTestsConfiguration.class)
@Transactional
public class ResourceRecorderTest {

    @Autowired
    private ResourceRecorder resourceRecorder;
    @Autowired
    private DayStatsDao dayStatsDao;
    @Autowired
    private ResourceDao resourceDao;

    public List<LogRecord> getSomeRecords() {
        List<LogRecord> records = new ArrayList<>();

        LogRecord logRecord = new LogRecord();
        logRecord.setUserId("user1");
        logRecord.setMethod("GET");
        logRecord.setResource("/rest/scores");
        logRecord.setResponseTime(0.02);
        logRecord.setResultCode("200");
        logRecord.setTimestamp(DateUtils.setHours(new Date(), 3));
        records.add(logRecord);

        logRecord = new LogRecord();
        logRecord.setUserId("user2");
        logRecord.setMethod("POST");
        logRecord.setResource("/rest/compose");
        logRecord.setResponseTime(0.03);
        logRecord.setResultCode("200");
        logRecord.setTimestamp(DateUtils.setHours(new Date(), 3));
        records.add(logRecord);

        logRecord = new LogRecord();
        logRecord.setUserId("user3");
        logRecord.setMethod("GET");
        logRecord.setResource("/rest/scores");
        logRecord.setResponseTime(0.02);
        logRecord.setResultCode("500");
        logRecord.setTimestamp(DateUtils.setHours(new Date(), 4));
        records.add(logRecord);

        return records;
    }

    @Test
    public void record() {
        DayStats dayStats = new DayStats();
        dayStats.setDate(new Date());
        dayStatsDao.save(dayStats);

        RecorderContext recorderContext = new RecorderContext(new Subject("1", "testsuite"));
        recorderContext.setCurrentDayStats(dayStats);

        resourceRecorder.initialize();
        for (LogRecord logRecord : getSomeRecords()) {
            resourceRecorder.record(logRecord, recorderContext);
        }
        resourceRecorder.store(new Date(), recorderContext);

        {
            List<Resource> resources = resourceDao.getPopular(dayStats, 5, "Path");

            MatcherAssert.assertThat(resources, CoreMatchers.notNullValue());
            MatcherAssert.assertThat(resources.size(), CoreMatchers.is(2));

            MatcherAssert.assertThat(resources.stream().map(r -> r.getTextValue()).collect(Collectors.toList()),
                    CoreMatchers.hasItems("GET:/rest/scores", "POST:/rest/compose"));
            MatcherAssert.assertThat(resources.stream().map(r -> r.getAverageResponseTime()).collect(Collectors.toList()),
                    CoreMatchers.hasItems(0.03, 0.02));
        }

        {
            List<Resource> resources = resourceDao.getPopular(dayStats, 5, "Path", "user2");

            MatcherAssert.assertThat(resources, CoreMatchers.notNullValue());
            MatcherAssert.assertThat(resources.size(), CoreMatchers.is(1));
        }
    }
}
