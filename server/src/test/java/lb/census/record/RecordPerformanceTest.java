package lb.census.record;

import lb.census.dao.DayStatsDao;
import lb.census.dao.SubjectDao;
import lb.census.dao.TotalActivityPerHourDao;
import lb.census.dao.UserActivityPerHourDao;
import lb.census.model.Subject;
import lb.census.record.log.JhksStandardLog;
import lb.census.record.recorders.ActivityPerHourRecorder;
import lb.census.record.recorders.GlobalStatsRecorder;
import lb.census.record.recorders.Recorder;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by philippe on 25/06/2017.
 */
//@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = TestScope.class)
//@TestPropertySource(properties = {
//        "census.data=file:///tmp",
//        "census.config=file:///config",
//        "logging.level.root=ERROR",
//        "logging.level.lb.census=TRACE",
//        "spring.jpa.show-sql=true",
//        "spring.jpa.properties.hibernate.format_sql=true"
//})
//@Transactional
public class RecordPerformanceTest {

    //@Autowired
    private SubjectDao subjectDao;
    //@Autowired
    private DayStatsDao dayStatsDao;
    //@Autowired
    private TotalActivityPerHourDao totalActivityPerHourDao;
    //@Autowired
    private UserActivityPerHourDao userActivityPerHourDao;

    //@Before
    public void createSubject() {
        subjectDao.save(new Subject("starwars", "Star Wars"));
    }

    private LogSet createLogData() throws IOException {
        String file = getClass().getResource("/localhost_access_log.2017-06-02.txt").getFile();

        DefaultLogSet defaultLogSet = new DefaultLogSet();
        defaultLogSet.setDate(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
        defaultLogSet.setLogRecordFactory(new JhksStandardLog());

        Stream<String> stream = Files.lines(new File(file).toPath());
        defaultLogSet.setLogData(Arrays.asList(stream));

        return defaultLogSet;
    }

    //@Test
    public void record() throws IOException {
        RecordServiceImpl recordService = new RecordServiceImpl();
        recordService.setSubjectDao(subjectDao);

        List<Recorder> recorders = new ArrayList<>();

        GlobalStatsRecorder globalStatsRecorder = new GlobalStatsRecorder();
        globalStatsRecorder.setDayStatsDao(dayStatsDao);
        //recorders.add(globalStatsRecorder);

        ActivityPerHourRecorder activityPerHourRecorder = new ActivityPerHourRecorder();
        activityPerHourRecorder.setDayStatsDao(dayStatsDao);
        activityPerHourRecorder.setTotalActivityPerHourDao(totalActivityPerHourDao);
        activityPerHourRecorder.setUserActivityPerHourDao(userActivityPerHourDao);
        recorders.add(activityPerHourRecorder);

        recordService.setRecorders(recorders);

        Subject subject = subjectDao.get("starwars");
        recordService.record(createLogData(), subject);
    }
}
