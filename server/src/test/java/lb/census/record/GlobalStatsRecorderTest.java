package lb.census.record;

import lb.census.CommonTestsConfiguration;
import lb.census.dao.DayStatsDao;
import lb.census.model.DayStats;
import lb.census.model.Subject;
import lb.census.record.log.LogRecord;
import lb.census.record.recorders.GlobalStatsRecorder;
import lb.census.record.recorders.RecorderContext;
import org.apache.commons.lang3.time.DateUtils;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(CommonTestsConfiguration.class)
public class GlobalStatsRecorderTest {

    @Autowired
    private GlobalStatsRecorder globalStatsRecorder;
    @Autowired
    private DayStatsDao dayStatsDao;

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
        logRecord.setResponseTime(0.04);
        logRecord.setResultCode("200");
        logRecord.setTimestamp(DateUtils.setHours(new Date(), 3));
        records.add(logRecord);

        logRecord = new LogRecord();
        logRecord.setUserId("user3");
        logRecord.setMethod("GET");
        logRecord.setResource("/rest/scores");
        logRecord.setResponseTime(0.04);
        logRecord.setResultCode("500");
        logRecord.setTimestamp(DateUtils.setHours(new Date(), 4));
        records.add(logRecord);

        logRecord = new LogRecord();
        logRecord.setUserId("user3");
        logRecord.setMethod("GET");
        logRecord.setResource("/rest/scores");
        logRecord.setResponseTime(0.06);
        logRecord.setResultCode("200");
        logRecord.setTimestamp(DateUtils.setHours(new Date(), 4));
        records.add(logRecord);

        return records;
    }

    @Test
    @Transactional
    public void record() {
        RecorderContext recorderContext = new RecorderContext(new Subject("1", "testsuite"));

        globalStatsRecorder.initialize();
        for (LogRecord logRecord : getSomeRecords()) {
            globalStatsRecorder.record(logRecord, recorderContext);
        }
        globalStatsRecorder.store(new Date(), recorderContext);

        // The DayStats instance is created by the globalStatsRecorder.
        DayStats dayStats = recorderContext.getCurrentDayStats();

        assertThat(dayStats.getTotalRequests()).isEqualTo(4);
        assertThat(dayStats.getTotalRequestsInError()).isOne();
        assertThat(dayStats.getMaxResponseTime()).isEqualTo(0.06);
        assertThat(dayStats.getMinResponseTime()).isEqualTo(0.02);
        assertThat(dayStats.getSubject()).isEqualTo("1");
        assertThat(dayStats.getAverageResponseTime()).isEqualTo(0.04);
    }
}
