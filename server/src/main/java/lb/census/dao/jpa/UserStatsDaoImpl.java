package lb.census.dao.jpa;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lb.census.dao.UserStatsDao;
import lb.census.model.UserStats;

@Repository
public class UserStatsDaoImpl extends BaseDaoImpl implements UserStatsDao {

    @Transactional
    @Override
    public void save(UserStats userStats) {
        if (userStats.getId() != null) {
            getEntityManager().merge(userStats);
        } else {
            getEntityManager().persist(userStats);
        }
    }

    @Transactional
    @Override
    public void delete(UserStats userStats) {
        EntityManager entityManager = getEntityManager();
        // we must reload the entity if necessary
        if (!entityManager.contains(userStats)) {
            userStats = entityManager.find(UserStats.class, userStats.getId());
        }
        entityManager.remove(userStats);
    }
}
