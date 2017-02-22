package lb.census.dao.jpa;

import lb.census.dao.ReportDao;
import lb.census.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
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
    public List<DayStatsReport> listDailyStats(Date from, Date until, String subject, String userCategoryFilter) {
        LOGGER.trace("list daily stats with {}, {}, {}, {}", from, until, subject, userCategoryFilter);

        List<CategoryFilter> filters = CategoryFilter.parse(userCategoryFilter);

        // Deivide the category expressions in 2 lists:
        // in: The categories to include
        // out: The categories to exclude
        List<String> in = new ArrayList<>(filters.size());
        List<String> out = new ArrayList<>(filters.size());
        for (CategoryFilter filter : filters) {
            if (filter.isExclude()) {
                out.add(filter.getCategory());
            } else {
                in.add(filter.getCategory());
            }
        }

        // We actually build:
        // SELECT date, sum(totalRequests), avg(averageResponseTime)
        // FROM UserActivityPerHour a, User u
        // WHERE
        // a.dayStats.date between from, until
        // and a.userid = u.userid
        // and (u.category in inList)
        // and (u.category not in inList)
        // GROUP BY date
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        CriteriaQuery q = cb.createQuery();

        Root<UserActivityPerHour> hourRoot = q.from(UserActivityPerHour.class);
        Join<UserActivityPerHour, DayStats> join = hourRoot.join(hourRoot.getModel().getSingularAttribute("dayStats", DayStats.class));
        Root<User> userRoot = q.from(User.class);

        q = q.multiselect(join.get("date"), cb.sum(hourRoot.get("hits"))).where(
                cb.equal(hourRoot.get("userId"), userRoot.get("userId"))
                , cb.greaterThanOrEqualTo(join.get("date"), from)
                , cb.lessThanOrEqualTo(join.get("date"), until)
                , userRoot.get("category").in(in)
                , cb.not(userRoot.get("category").in(out))
        ).groupBy(join.get("date"));

        LOGGER.trace("Executing query");
        List qResult = getEntityManager().createQuery(q).getResultList();

        LOGGER.trace("Collecting results");
        List<DayStatsReport> result = new ArrayList<>(qResult.size());
        qResult.stream().forEach(o -> {
            Object[] row = (Object[]) o;
            DayStatsReport r = new DayStatsReport();
            r.setDate((Date) row[0]);
            r.setTotalRequests(((Long) row[1]).intValue());
            result.add(r);
        });
        return result;
    }
}
