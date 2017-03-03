package lb.census.api.stats.perday;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lb.census.rest.subjects.stats.OneDay;
import lb.census.rest.subjects.stats.OneDayDetails;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lb.census.api.CensusController;
import lb.census.dao.DayStatsDao;
import lb.census.dao.ResourceDao;
import lb.census.dao.TotalActivityPerHourDao;
import lb.census.dao.UserActivityPerHourDao;
import lb.census.dao.UserDao;
import lb.census.model.DayStats;
import lb.census.model.User;
import lb.census.utils.UserBaseReducer;

/**
 * Created by phili on 21/12/2015.
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/stats/{subject}")
public class DayController extends CensusController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DayController.class);

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

	@RequestMapping("/yesterday")
	public OneDay yesterday(@PathVariable("subject") String subject) {
		OneDay oneDay = new OneDay();
		if (subject == null) {
			return oneDay;
		}

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -1);
		c = DateUtils.truncate(c, Calendar.DAY_OF_MONTH);

		LOGGER.info("Getting the day statistics for {} of {}", c.getTime(), subject);
		DayStats dayStats = dayStatsDao.getDayStats(c.getTime(), subject);
		if (dayStats == null) {
			// return empty data
			return oneDay;
		}

		return map(dayStats, oneDay);
	}

	@RequestMapping("/list/{total}")
	public List<OneDay> list(@PathVariable("subject") String subject, @PathVariable("total") int total) {
		if (subject == null || total == 0) {
			return new ArrayList<>(0);
		}

		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		yesterday = DateUtils.truncate(yesterday, Calendar.DAY_OF_MONTH);

		Calendar wayBack = Calendar.getInstance();
		wayBack.add(Calendar.DAY_OF_MONTH, 0 - (total + 1));
		wayBack = DateUtils.truncate(wayBack, Calendar.DAY_OF_MONTH);

		List<DayStats> list = dayStatsDao.list(wayBack.getTime(), yesterday.getTime(), subject);
		List<OneDay> result = new ArrayList<>();
		list.stream().forEach(d -> result.add(map(d, new OneDay())));

		return result;
	}

	@RequestMapping("/list/{from}/{total}")
	public List<OneDay> list(@PathVariable("subject") String subject, @PathVariable("from") String from,
			@PathVariable("total") int total) throws ParseException {
		if (subject == null || from == null || total == 0) {
			return new ArrayList<>(0);
		}

		Date fromDate = super.toDate(from);

		Calendar startDateFrom = Calendar.getInstance();
		startDateFrom.setTime(fromDate);
		startDateFrom = DateUtils.truncate(startDateFrom, Calendar.DAY_OF_MONTH);

		Calendar wayBack = Calendar.getInstance();
		wayBack.add(Calendar.DAY_OF_MONTH, 0 - (total + 1));
		wayBack = DateUtils.truncate(wayBack, Calendar.DAY_OF_MONTH);

		List<DayStats> list = dayStatsDao.list(wayBack.getTime(), startDateFrom.getTime(), subject);
		List<OneDay> result = new ArrayList<>();
		list.stream().forEach(d -> result.add(map(d, new OneDay())));

		OneDay max = result.stream().max((d1, d2) -> d1.totalRequests.compareTo(d2.totalRequests)).get();
		result.stream().forEach(d -> {
			d.share = d.totalRequests * 100 / max.totalRequests;
		});

		return result;
	}

	/**
	 * Returns the data for a specific day. This will contains 
	 * 
	 * @param subject
	 * @param from
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/details/{from}")
	public OneDayDetails details(@PathVariable("subject") String subject, @PathVariable("from") String from)
			throws ParseException {
		OneDayDetails oneDayDetails = new OneDayDetails();
		if (subject == null || from == null) {
			return oneDayDetails;
		}

		Date fromDate = super.toDate(from);
		LOGGER.info("Getting the day details for {} -> {}", from, fromDate);

		// make sure to truncate the date
		DayStats dayStats = dayStatsDao.getDayStats(DateUtils.truncate(fromDate, Calendar.DAY_OF_MONTH), subject);
		if (dayStats == null) {
			return oneDayDetails;
		}
		map(dayStats, oneDayDetails);

		List<User> allUsers = userDao.getUsers(subject);

		// First collect the recorded users. The user base reducer helps
		// identifying the users that are expected but
		// were not recorded (= no activity).
		oneDayDetails.recordedUsers = new ArrayList<>(allUsers.size());
		UserBaseReducer userBaseReducer = new UserBaseReducer(allUsers);
		dayStats.getUserStatsList().stream().forEach(userStats -> {
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
			oneDayDetails.recordedUsers.add(oneUser);
		});
		// Collect the missing users.
		oneDayDetails.missingUsers = new ArrayList<>();
		userBaseReducer.remaining().stream().forEach(user -> {
			OneUser oneUser = new OneUser();
			oneUser.userId = user.getUserId();
			oneUser.userName = user.getName();
			oneDayDetails.missingUsers.add(oneUser);
		});

		// collect activity per hour
		oneDayDetails.activityPerHour = new ArrayList<>();
		totalActivityPerHourDao.getDayActivity(dayStats).stream().forEach(activity -> {
			OneHour oneHour = new OneHour();
			oneHour.hour = activity.getHour();
			oneHour.totalRequests = activity.getHits();
			oneDayDetails.activityPerHour.add(oneHour);
		});

		// collect top ten resources
		oneDayDetails.popularResources = new ArrayList<>();
		resourceDao.getPopular(dayStats, 15, "Path").forEach(resource -> {
			OneResource oneResource = new OneResource();
			oneResource.path = resource.getTextValue();
			oneResource.hits = resource.getHits();
			oneDayDetails.popularResources.add(oneResource);
		});

		// set the shares
		OneHour maxHour = oneDayDetails.activityPerHour.stream()
				.max((o1, o2) -> o1.totalRequests.compareTo(o2.totalRequests)).get();
		oneDayDetails.activityPerHour.stream().forEach(o -> {
			o.share = o.totalRequests * 100 / maxHour.totalRequests;
		});
		OneUser maxUser = oneDayDetails.recordedUsers.stream()
				.max((u1, u2) -> u1.totalRequests.compareTo(u2.totalRequests)).get();
		oneDayDetails.recordedUsers.stream().forEach(u -> {
			u.share = u.totalRequests * 100 / maxUser.totalRequests;
		});

		return oneDayDetails;
	}

	@RequestMapping("/details/{from}/user/{user}")
	public OneUserDetails userDetails(@PathVariable("subject") String subject, @PathVariable("from") String from,
			@PathVariable("user") String user) throws ParseException {
		OneUserDetails oneUserDetails = new OneUserDetails();
		if (subject == null || from == null) {
			return oneUserDetails;
		}

		Date fromDate = DateUtils.truncate(super.toDate(from), Calendar.DAY_OF_MONTH);
		oneUserDetails.date = fromDate;
		oneUserDetails.userId = user;
		User userData = userDao.getUser(user);
		if (userData != null) {
			oneUserDetails.userName = userData.getName();
		}

		DayStats dayStats = dayStatsDao.getDayStats(fromDate, subject);
		if (dayStats == null) {
			return oneUserDetails;
		}

		// collect the activity per hour of the user
		// TODO: should a map not be used here?
		oneUserDetails.activityPerHour = new ArrayList<>();
		userActivityPerHourDao.getActivities(fromDate, fromDate, user).stream().forEach(activity -> {
			OneHour oneHour = new OneHour();
			oneHour.hour = activity.getHour();
			oneHour.totalRequests = activity.getHits();
			oneUserDetails.activityPerHour.add(oneHour);
		});
		// set the shares
		OneHour maxHour = oneUserDetails.activityPerHour.stream()
				.max((o1, o2) -> o1.totalRequests.compareTo(o2.totalRequests)).get();
		oneUserDetails.activityPerHour.stream().forEach(o -> {
			o.share = o.totalRequests * 100 / maxHour.totalRequests;
		});

		// collect top ten resources
		oneUserDetails.popularResources = new ArrayList<>();
		resourceDao.getPopular(dayStats, 15, "Path", user).forEach(resource -> {
			OneResource oneResource = new OneResource();
			oneResource.path = resource.getTextValue();
			oneResource.hits = resource.getHits();
			oneUserDetails.popularResources.add(oneResource);
		});

		return oneUserDetails;
	}

	private OneDay map(DayStats dayStats, OneDay oneDay) {
		oneDay.date = dayStats.getDate();
		oneDay.averageResponseTime = dayStats.getAverageResponseTime();
		oneDay.maxResponseTime = dayStats.getMaxResponseTime();
		oneDay.minResponseTime = dayStats.getMinResponseTime();
		oneDay.totalRequests = dayStats.getTotalRequests();
		oneDay.totalRequestsInError = dayStats.getTotalRequestsInError();
		oneDay.totalUserIds = dayStats.getTotalUserIds();
		return oneDay;
	}
}
