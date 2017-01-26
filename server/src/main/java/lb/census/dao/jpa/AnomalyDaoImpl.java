package lb.census.dao.jpa;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import lb.census.dao.AnomalyDao;
import lb.census.model.Anomaly;

@Repository
public class AnomalyDaoImpl extends BaseDaoImpl implements AnomalyDao {

    @Override
    public void save(Anomaly anomaly) {
        if (anomaly.getId() != null) {
            getEntityManager().merge(anomaly);
        } else {
            getEntityManager().persist(anomaly);
        }
    }

    @Override
    public List<Anomaly> getAllAnomalies() {
        TypedQuery<Anomaly> query = getEntityManager().createQuery(
                "SELECT a FROM Anomaly a", Anomaly.class);
        return query.getResultList();
    }
}
