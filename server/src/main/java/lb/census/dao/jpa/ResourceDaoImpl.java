package lb.census.dao.jpa;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lb.census.dao.ResourceDao;
import lb.census.model.DayStats;
import lb.census.model.Resource;

/**
 * Created by philippe on 24/04/16.
 */
@Repository
public class ResourceDaoImpl extends BaseDaoImpl implements ResourceDao {

    private static final String NULL_USER = "census";

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void save(Resource resource) {
        if (resource.getUserId() == null) {
            resource.setUserId(NULL_USER);
        }
        if (resource.getId() != null) {
            getEntityManager().merge(resource);
        } else {
            getEntityManager().persist(resource);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Override
    public List<Resource> getPopular(int top, String name) {
        TypedQuery<Resource> query = getEntityManager().createQuery(
                "SELECT r FROM Resource r WHERE r.name = :n and r.userId = :u order by r.hits desc", Resource.class);
        return query.setParameter("n", name).setParameter("u", NULL_USER).setFirstResult(top).getResultList();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Override
    public List<Resource> getPopular(DayStats dayStats, int top, String name) {
        TypedQuery<Resource> query = getEntityManager().createQuery(
                "select r from Resource r where r.name = :n and r.userId = :u and r.dayStats = :d order by r.hits desc", Resource.class);
        query.setParameter("n", name);
        query.setParameter("d", dayStats);
        query.setParameter("u", NULL_USER);
        return query.setMaxResults(top).getResultList();
    }

    @Override
    public List<Resource> getPopular(DayStats dayStats, int top, String name, String userId) {
        TypedQuery<Resource> query = getEntityManager().createQuery(
                "select r from Resource r where r.name = :n and r.userId = :u and r.dayStats = :d order by r.hits desc", Resource.class);
        query.setParameter("n", name);
        query.setParameter("d", dayStats);
        query.setParameter("u", userId);
        return query.setMaxResults(top).getResultList();
    }
}
