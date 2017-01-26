package lb.census.dao.jpa;

import java.util.Date;
import java.util.List;

import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lb.census.dao.DayStatsDao;
import lb.census.model.DayStats;

@Repository
public class DayStatsDaoImpl extends BaseDaoImpl implements DayStatsDao {

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void save(DayStats dayStats) {
        if (dayStats.getId() != null) {
            getEntityManager().merge(dayStats);
        } else {
            //dayStats.setId(new TemporalIdGenerator().generate());
            getEntityManager().persist(dayStats);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void delete(DayStats dayStats) {
        // we must reload the entity if necessary
        if (!getEntityManager().contains(dayStats)) {
            dayStats = getEntityManager().find(DayStats.class, dayStats.getId());
        }
        getEntityManager().remove(dayStats);
    }

    @Transactional(readOnly = true)
    @Override
    public DayStats getDayStats(Date date, String subject) {
        TypedQuery<DayStats> query = getEntityManager().createQuery(
                "SELECT d FROM DayStats d WHERE d.date = :d and subject = :s", DayStats.class);
        query.setParameter("d", date, TemporalType.DATE);
        query.setParameter("s", subject);
        List<DayStats> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DayStats> list(Date from, Date until, String subject) {
        TypedQuery<DayStats> query = getEntityManager().createQuery(
                "SELECT d FROM DayStats d WHERE d.subject = :s and d.date >= :d and d.date <= :dd order by d.date asc", DayStats.class);
        query.setParameter("d", from, TemporalType.DATE);
        query.setParameter("dd", until, TemporalType.DATE);
        query.setParameter("s", subject);
        return query.getResultList();
    }
}
