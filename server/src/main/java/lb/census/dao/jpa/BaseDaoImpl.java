package lb.census.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author psc
 */
public class BaseDaoImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
