package lb.census.dao;

import lb.census.model.*;
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
 * Created by philippeschottey on 14/02/2017.
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
public class ReportDaoTest {

    @Autowired
    private DayStatsDao dayStatsDao;

    @Autowired
    private UserActivityPerHourDao userActivityPerHourDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ReportDao reportDao;

    @Before
    public void prepareData() {
        // prepare data
        {
            User user = new User();
            user.setUserId("a");
            user.setName("AaA");
            user.setCategory("VIP");
            userDao.save(user);
        }
        {
            User user = new User();
            user.setUserId("c");
            user.setName("CcC");
            user.setCategory("VIP");
            userDao.save(user);
        }
        {
            User user = new User();
            user.setUserId("b");
            user.setName("BbB");
            user.setCategory("LAZY");
            userDao.save(user);
        }

        DayStats dayStats = new DayStats();
        dayStats.setAverageResponseTime(2.0);
        dayStats.setTotalRequests(1000);
        dayStats.setDate(DateUtils.addDays(new Date(), -2));
        dayStats.setSubject("test");
        dayStatsDao.save(dayStats);

        UserActivityPerHour userActivityPerHour = new UserActivityPerHour();
        userActivityPerHour.setUserId("a");
        userActivityPerHour.setDayStats(dayStats);
        userActivityPerHour.setHits(100);
        userActivityPerHourDao.save(userActivityPerHour);

        userActivityPerHour = new UserActivityPerHour();
        userActivityPerHour.setUserId("c");
        userActivityPerHour.setDayStats(dayStats);
        userActivityPerHour.setHits(150);
        userActivityPerHourDao.save(userActivityPerHour);

    }

    @Test
    public void listTest() {
        List<CategoryFilter> categoryFilters = new ArrayList<>();
        categoryFilters.add(new CategoryFilter("VIP"));

        List<DayStatsReport> reports = reportDao.listDailyStats(DateUtils.addDays(new Date(), -5), new Date(), "test", categoryFilters);
        assertThat(reports.size(), is(1));

        assertThat(reports.get(0).getTotalRequests(), is(250));
    }
}
