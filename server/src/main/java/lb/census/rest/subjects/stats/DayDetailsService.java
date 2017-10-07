package lb.census.rest.subjects.stats;

import lb.census.dao.*;
import lb.census.model.DayStats;
import lb.census.model.User;
import lb.census.rest.shared.UserBaseReducer;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by philippeschottey on 05/03/2017.
 */
@Service
public class DayDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger("CENSUS_CONTROLLERS");

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

    public OneDayDetails getDetails(String subject, Date date) {
        LOGGER.info("Getting the day details for {} -> {}", subject, date);

        OneDayDetails oneDayDetails = new OneDayDetails();
        if (subject == null || date == null) {
            return oneDayDetails;
        }

        date = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);

        // make sure to truncate the date
        DayStats dayStats = dayStatsDao.getDayStats(date, subject);
        if (dayStats == null) {
            return oneDayDetails;
        }
        OneDay.map(dayStats, oneDayDetails);

        List<User> allUsers = userDao.getUsers(subject);

        // First collect the recorded users. The user base reducer helps
        // identifying the users that are expected but
        // were not recorded (= no activity).
        UserBaseReducer userBaseReducer = new UserBaseReducer(allUsers);
        oneDayDetails.recordedUsers = dayStats.getUserStatsList().stream().map(userStats -> {
            User user = userBaseReducer.reduce(userStats.getUserId());
            OneUser oneUser = new OneUser();
            oneUser.totalRequests = userStats.getTotalRequests();
            oneUser.totalRequestsInError = userStats.getTotalRequestsInError();
            oneUser.userId = userStats.getUserId();
            if (user != null) {
                oneUser.userName = user.getName();
            } else {
                oneUser.userName = "Unknown";
            }
            return oneUser;
        }).collect(Collectors.toList());

        // Collect the missing users.
        oneDayDetails.missingUsers = userBaseReducer.remaining().stream().map(user -> {
            OneUser oneUser = new OneUser();
            oneUser.userId = user.getUserId();
            oneUser.userName = user.getName();
            return oneUser;
        }).collect(Collectors.toList());

        // Collect activity per hour
        oneDayDetails.activityPerHour = totalActivityPerHourDao.getDayActivity(dayStats).stream().map(activity -> {
            OneHour oneHour = new OneHour();
            oneHour.hour = activity.getHour();
            oneHour.totalRequests = activity.getHits();
            return oneHour;
        }).collect(Collectors.toList());

        // collect top ten resources
        oneDayDetails.popularResources = resourceDao.getPopular(dayStats, 15, "Path").stream().map(resource -> {
            OneResource oneResource = new OneResource();
            oneResource.path = resource.getTextValue();
            oneResource.hits = resource.getHits();
            return oneResource;
        }).collect(Collectors.toList());

        return oneDayDetails;
    }

    public OneUserDetails getUserDetails(String subject, Date date, String userId) {
        OneUserDetails oneUserDetails = new OneUserDetails();
        oneUserDetails.date = date;
        oneUserDetails.userId = userId;

        User user = userDao.getUser(userId);
        if (user != null) {
            oneUserDetails.userName = user.getName();
        }

        date = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
        // make sure to truncate the date
        DayStats dayStats = dayStatsDao.getDayStats(date, subject);
        if (dayStats == null) {
            return oneUserDetails;
        }

        // Collect activity per hour
        oneUserDetails.activityPerHour = userActivityPerHourDao.getActivities(dayStats, userId).stream().map(activity -> {
            OneHour oneHour = new OneHour();
            oneHour.hour = activity.getHour();
            oneHour.totalRequests = activity.getHits();
            oneHour.averageResponseTime = activity.getAverageResponseTime();
            return oneHour;
        }).collect(Collectors.toList());

        return oneUserDetails;
    }
}
