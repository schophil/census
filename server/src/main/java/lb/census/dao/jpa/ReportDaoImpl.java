package lb.census.dao.jpa;

import lb.census.dao.ReportDao;
import lb.census.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by philippeschottey on 13/02/2017.
 */
@Repository
public class ReportDaoImpl extends BaseDaoImpl implements ReportDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportDaoImpl.class);

    @Transactional(readOnly = true)
    @Override
    public List<DayStatsReport> listDailyStats(Date from, Date until, String subject, List<CategoryFilter> categoryFilters) {
        LOGGER.trace("list daily stats with {}, {}, {}, {}", from, until, subject, categoryFilters);

        // Divide the category expressions in 2 lists:
        // in: The categories to include
        // out: The categories to exclude
        List<String> in = categoryFilters.stream()
                .filter(f -> !f.isExclude())
                .map(f -> f.getCategory())
                .collect(Collectors.toList());

        List<String> out = categoryFilters.stream()
                .filter(f -> f.isExclude())
                .map(f -> f.getCategory())
                .collect(Collectors.toList());

        // We actually build something like:
        // SELECT date, sum(totalRequests), avg(averageResponseTime)
        // FROM UserActivityPerHour a, User u
        // WHERE
        // a.dayStats.date between from, until
        // and a.userid = u.userid
        // and (u.category in inList)
        // and (u.category not in inList)
        // GROUP BY date
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        CriteriaQuery<Object[]> q = cb.createQuery(Object[].class);

        Root<UserActivityPerHour> hourRoot = q.from(UserActivityPerHour.class);
        Join<UserActivityPerHour, DayStats> join = hourRoot.join(
                hourRoot.getModel().getSingularAttribute("dayStats", DayStats.class));
        Root<User> userRoot = q.from(User.class);

        q = q.multiselect(
                join.get("date"),
                cb.sum(hourRoot.get("totalRequests")),
                cb.avg(hourRoot.get("averageResponseTime")),
                cb.countDistinct(hourRoot.get("userId"))
        );

        List<Predicate> conditions = new ArrayList<>();
        conditions.add(cb.equal(join.get("subject"), subject));
        conditions.add(cb.equal(hourRoot.get("userId"), userRoot.get("userId")));
        conditions.add(cb.greaterThanOrEqualTo(join.get("date"), from));
        conditions.add(cb.lessThanOrEqualTo(join.get("date"), until));
        if (!in.isEmpty()) {
            conditions.add(userRoot.get("category").in(in));
        }

        if (!out.isEmpty()) {
            conditions.add(cb.not(userRoot.get("category").in(out)));
        }
        q.where(cb.and(conditions.toArray(new Predicate[conditions.size()])));

        q.groupBy(join.get("date"));
        q.orderBy(cb.asc(join.get("date")));

        LOGGER.trace("Executing query and collecting results");
        return getEntityManager().createQuery(q).getResultList()
                .stream()
                .map(this::read)
                .collect(Collectors.toList());
    }

    private DayStatsReport read(Object[] row) {
        DayStatsReport r = new DayStatsReport();
        r.setDate((Date) row[0]);
        r.setTotalRequests(((Long) row[1]).intValue());
        r.setAverageResponseTime((Double) row[2]);
        r.setTotalUserIds(((Long) row[3]).intValue());
        return r;
    }
}
