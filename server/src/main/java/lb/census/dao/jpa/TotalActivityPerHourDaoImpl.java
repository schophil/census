package lb.census.dao.jpa;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lb.census.dao.TotalActivityPerHourDao;
import lb.census.model.DayStats;
import lb.census.model.TotalActivityPerHour;

/**
 *
 * @author psc
 */
@Repository
public class TotalActivityPerHourDaoImpl extends BaseDaoImpl implements TotalActivityPerHourDao {

    @Transactional
    @Override
    public void save(TotalActivityPerHour dayActivity) {
        if (dayActivity.getId() != null) {
            getEntityManager().merge(dayActivity);
        } else {
            //dayStats.setId(new TemporalIdGenerator().generate());
            getEntityManager().persist(dayActivity);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<TotalActivityPerHour> getDayActivity(Date date) {
        TypedQuery<TotalActivityPerHour> query = getEntityManager().createQuery(
                "SELECT a FROM TotalActivityPerHour a, DayStats d WHERE d.date = :date and a.dayStats = d", TotalActivityPerHour.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    @Override
    public List<TotalActivityPerHour> getDayActivity(DayStats dayStats) {
        TypedQuery<TotalActivityPerHour> query = getEntityManager().createQuery(
                "SELECT a FROM TotalActivityPerHour a WHERE a.dayStats = :d", TotalActivityPerHour.class);
        query.setParameter("d", dayStats);
        return query.getResultList();
    }
}
