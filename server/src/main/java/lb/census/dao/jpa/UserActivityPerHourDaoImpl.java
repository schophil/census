package lb.census.dao.jpa;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import lb.census.model.DayStats;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lb.census.dao.UserActivityPerHourDao;
import lb.census.model.UserActivityPerHour;

/**
 * Created by philippe on 28/10/14.
 */
@Repository
public class UserActivityPerHourDaoImpl extends BaseDaoImpl implements UserActivityPerHourDao {

    @Transactional
    @Override
    public UserActivityPerHour save(UserActivityPerHour userActivity) {
        if (userActivity.getId() != null) {
            return getEntityManager().merge(userActivity);
        } else {
            getEntityManager().persist(userActivity);
            return userActivity;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserActivityPerHour> getActivities(Date dateTime) {
        String queryText = "SELECT a FROM UserActivityPerHour a, DayStats d WHERE "
                + "d.date = :date and a.dayStats = d and a.hour = :hour order by a.hits desc";

        TypedQuery<UserActivityPerHour> query = getEntityManager().createQuery(queryText, UserActivityPerHour.class);

        query.setParameter("date", dateTime);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        query.setParameter("hour", calendar.get(Calendar.HOUR_OF_DAY));

        return query.getResultList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserActivityPerHour> getActivities(Date from, Date to, String user) {
        String queryText = "SELECT a FROM UserActivityPerHour a WHERE " +
                "a.dayStats.date >= :fromDate and a.dayStats.date <= :toDate " +
                "and a.userId = :user " +
                "order by a.dayStats.date desc, a.hour asc";

        TypedQuery<UserActivityPerHour> query = getEntityManager().createQuery(queryText, UserActivityPerHour.class);

        query.setParameter("fromDate", from);
        query.setParameter("toDate", to);
        query.setParameter("user", user);

        return query.getResultList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserActivityPerHour> getActivities(DayStats dayStats, String user) {
        String queryText = "SELECT a FROM UserActivityPerHour a WHERE " +
                "a.dayStats = :day " +
                "and a.userId = :user " +
                "order by a.dayStats.date desc, a.hour asc";

        TypedQuery<UserActivityPerHour> query = getEntityManager().createQuery(queryText, UserActivityPerHour.class);

        query.setParameter("day", dayStats);
        query.setParameter("user", user);

        return query.getResultList();
    }
}
