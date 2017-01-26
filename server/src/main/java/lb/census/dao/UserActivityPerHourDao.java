package lb.census.dao;

import java.util.Date;
import java.util.List;

import lb.census.model.UserActivityPerHour;

/**
 * Created by philippe on 28/10/14.
 */
public interface UserActivityPerHourDao {

    UserActivityPerHour save(UserActivityPerHour userActivity);

    List<UserActivityPerHour> getActivities(Date dateTime);

    List<UserActivityPerHour> getActivities(Date from, Date to, String users);
}
