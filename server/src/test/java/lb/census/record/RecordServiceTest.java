package lb.census.record;

import lb.census.TestScope;
import lb.census.dao.DayStatsDao;
import lb.census.dao.SubjectDao;
import lb.census.model.Subject;
import lb.census.record.filters.PatternFilter;
import lb.census.record.log.ApacheLogFormat;
import lb.census.record.recorders.RecorderContext;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by philippeschottey on 05/03/2017.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestScope.class)
@TestPropertySource(properties = {
        "census.data=file:///tmp",
        "census.config=file:///config",
        "logging.level.root=ERROR",
        "logging.level.lb.census=TRACE",
        "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.format_sql=true"
})
@Transactional
public class RecordServiceTest {

    @Autowired
    private RecordService recordService;
    @Autowired
    private SubjectDao subjectDao;
    @Autowired
    private DayStatsDao dayStatsDao;

    private LogSet createLogData() {
        DefaultLogSet defaultLogSet = new DefaultLogSet();
        defaultLogSet.setDate(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
        defaultLogSet.setLogRecordFactory(new ApacheLogFormat());

        PatternFilter patternFilter = new PatternFilter();
        patternFilter.setRegexp(".*(darthvader)+.*");
        defaultLogSet.setFilters(Arrays.asList(patternFilter.invert()));

        Stream<String> stream = Stream.of(
                "127.0.0.1 - darthvader [02/Apr/2014:00:10:40 +0200] \"GET /manager/text/undeploy?path=/alor HTTP/1.1\" 200 61 0.2",
                "127.0.0.1 - luke [02/Apr/2014:00:15:40 +0200] \"GET /manager/text/undeploy?path=/alor HTTP/1.1\" 200 61 0.3",
                "127.0.0.1 - luke [02/Apr/2014:01:20:40 +0200] \"GET /manager/text/undeploy?path=/alor HTTP/1.1\" 200 45 0.2",
                "127.0.0.1 - luke [02/Apr/2014:01:30:40 +0200] \"GET /manager/text/undeploy?path=/alor HTTP/1.1\" 200 61 0.2",
                "127.0.0.1 - solo [02/Apr/2014:02:45:40 +0200] \"GET /manager/text/undeploy?path=/alor HTTP/1.1\" 200 61 0.5",
                "127.0.0.1 - luke [02/Apr/2014:02:50:40 +0200] \"GET /manager/text/undeploy?path=/alor HTTP/1.1\" 200 50 0.4",
                "127.0.0.1 - solo [02/Apr/2014:03:51:40 +0200] \"GET /manager/text/undeploy?path=/alor HTTP/1.1\" 200 61 0.3",
                "127.0.0.1 - darthvader [02/Apr/2014:04:45:40 +0200] \"GET /manager/text/undeploy?path=/alor HTTP/1.1\" 200 100 0.2",
                "127.0.0.1 - solo [02/Apr/2014:05:30:40 +0200] \"GET /manager/text/undeploy?path=/alor HTTP/1.1\" 200 61 0.2",
                "127.0.0.1 - solo [02/Apr/2014:05:35:40 +0200] \"GET /manager/text/undeploy?path=/alor HTTP/1.1\" 200 61 0.2",
                "127.0.0.1 - luke [02/Apr/2014:06:45:40 +0200] \"GET /manager/text/undeploy?path=/alor HTTP/1.1\" 200 61 0.23",
                "127.0.0.1 - darthvader [02/Apr/2014:07:23:40 +0200] \"GET /manager/text/undeploy?path=/alor HTTP/1.1\" 200 30 0.6",
                "127.0.0.1 - solo [02/Apr/2014:07:39:40 +0200] \"GET /manager/text/undeploy?path=/alor HTTP/1.1\" 200 61 0.7",
                "127.0.0.1 - darthvader [02/Apr/2014:08:00:40 +0200] \"GET /manager/text/undeploy?path=/alor HTTP/1.1\" 200 15 0.2"
        );
        defaultLogSet.setLogData(Arrays.asList(stream));

        return defaultLogSet;
    }

    @Before
    public void createSubject() {
        subjectDao.save(new Subject("starwars", "Star Wars"));
    }

    @Test
    public void recordAndRead() {
        Subject subject = subjectDao.get("starwars");
        RecorderContext recorderContext = recordService.record(createLogData(), subject);
        assertThat(recorderContext.getImported(), is(10));
        assertThat(recorderContext.getFiltered(), is(4));
    }

    @Test
    public void recordAndOverwrite() {
        Subject subject = subjectDao.get("starwars");
        RecorderContext recorderContext = recordService.record(createLogData(), subject);
        assertThat(recorderContext.getImported(), is(10));
        assertThat(recorderContext.getFiltered(), is(4));

        recorderContext = recordService.record(createLogData(), subject);
        assertThat(recorderContext.getImported(), is(10));
        assertThat(recorderContext.getFiltered(), is(4));

        dayStatsDao.getDayStats(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH), subject.getId());
    }
}
