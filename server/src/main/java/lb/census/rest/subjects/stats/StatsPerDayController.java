package lb.census.rest.subjects.stats;

import lb.census.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by philippeschottey on 01/03/2017.
 */
@RestController
@RequestMapping("/rest/subjects/{subject}/stats")
public class StatsPerDayController {

    @Autowired
    private DayStatsDao dayStatsDao;
    @Autowired
    private TotalActivityPerHourDao totalActivityPerHourDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ResourceDao resourceDao;
    @Autowired
    private UserActivityPerHourDao userActivityPerHourDao;

    @RequestMapping("/days")
    public List<OneDay> list(@RequestParam String category, @RequestParam String period) {

        return null;
    }
}
