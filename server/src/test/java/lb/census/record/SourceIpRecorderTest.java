package lb.census.record;

import lb.census.CommonTestsConfiguration;
import lb.census.dao.DayStatsDao;
import lb.census.dao.SourceIpDao;
import lb.census.dao.UserActivityPerHourDao;
import lb.census.model.DayStats;
import lb.census.model.SourceIp;
import lb.census.model.Subject;
import lb.census.record.log.LogRecord;
import lb.census.record.recorders.RecorderContext;
import lb.census.record.recorders.SourceIpRecorder;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(CommonTestsConfiguration.class)
public class SourceIpRecorderTest {

    @Autowired
    private SourceIpRecorder sourceIpRecorder;
    @Autowired
    private DayStatsDao dayStatsDao;
    @Autowired
    private SourceIpDao sourceIpDao;
    @Autowired
    private UserActivityPerHourDao userActivityPerHourDao;

    public List<LogRecord> prepareSomeRecords() {
        List<LogRecord> logRecords = new ArrayList<>();
        logRecords.add(new LogRecord("", 0.02, "200", "user1", "192.0.0.1", new Date(), "/foo", "GET"));
        logRecords.add(new LogRecord("", 0.02, "200", "user2", "10.0.0.1", new Date(), "/foo", "GET"));
        logRecords.add(new LogRecord("", 0.02, "200", "user1", "192.0.0.2", new Date(), "/foo", "GET"));
        logRecords.add(new LogRecord("", 0.02, "200", "user2", "10.0.0.1", new Date(), "/foo", "GET"));
        logRecords.add(new LogRecord("", 0.02, "200", "user3", "192.168.12.12", new Date(), "/foo", "GET"));
        logRecords.add(new LogRecord("", 0.02, "200", "user3", "192.168.12.12", new Date(), "/foo", "GET"));
        return logRecords;
    }

    @Before
    public void record() {
        DayStats dayStats = new DayStats();
        dayStats.setDate(DateUtils.addDays(new Date(), -3));
        dayStatsDao.save(dayStats);

        RecorderContext recorderContext = new RecorderContext(new Subject("1", "testsuite"));
        recorderContext.setCurrentDayStats(dayStats);

        sourceIpRecorder.initialize();
        for (LogRecord logRecord : prepareSomeRecords()) {
            sourceIpRecorder.record(logRecord, recorderContext);
        }
        sourceIpRecorder.store(DateUtils.addDays(new Date(), -3), recorderContext);
    }

    @Test
    public void validateRecordResult() {
        List<SourceIp> ips = sourceIpDao.getAllSourceIPs(5);
        assertThat(ips).isNotNull();
        assertThat(ips).isNotEmpty();
        assertThat(ips.size()).isEqualTo(4);

        assertThat(ips.stream().map(ip -> ip.getIp()).collect(Collectors.toList()))
                .containsExactlyInAnyOrder("192.0.0.1", "10.0.0.1", "192.0.0.2", "192.168.12.12");
        assertThat(ips.stream().map(ip -> ip.getUserId()).collect(Collectors.toList()))
                .containsExactlyInAnyOrder("user1", "user1", "user2", "user3");

        assertThat(ips.get(0).getLastUsed()).isInSameDayAs(DateUtils.addDays(new Date(), -3));
    }

    @Test
    public void searchByWildcard() {
        List<SourceIp> user1Ips = sourceIpDao.searchSourceIPs("user1", 5);
        assertThat(user1Ips).isNotNull();
        assertThat(user1Ips).isNotEmpty();
        assertThat(user1Ips.size()).isEqualTo(2);

        List<SourceIp> ips = sourceIpDao.searchSourceIPs("168", 5);
        assertThat(ips).isNotNull();
        assertThat(ips).isNotEmpty();
        assertThat(ips.size()).isEqualTo(1);
        assertThat(ips.get(0).getUserId()).isEqualTo("user3");
        assertThat(ips.get(0).getIp()).isEqualTo("192.168.12.12");
    }

    @Test
    public void searchByUser() {
        List<SourceIp> ips = sourceIpDao.getSourceIPsFor("user1", 3);
        assertThat(ips).isNotNull();
        assertThat(ips).isNotEmpty();
        assertThat(ips.size()).isEqualTo(2);

        assertThat(ips.stream().map(ip -> ip.getIp()).collect(Collectors.toList()))
                .containsExactlyInAnyOrder("192.0.0.1", "192.0.0.2");
        assertThat(ips.stream().map(ip -> ip.getUserId()).collect(Collectors.toList()))
                .containsExactlyInAnyOrder("user1", "user1");
    }

    @Test
    public void delete() {
        int total = sourceIpDao.deleteSourceIPsOf(2);
        assertThat(total).isEqualTo(4);
        assertThat(sourceIpDao.getAllSourceIPs(5)).isEmpty();
    }
}
