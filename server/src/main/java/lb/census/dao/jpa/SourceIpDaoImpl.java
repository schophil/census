package lb.census.dao.jpa;

import lb.census.dao.SourceIpDao;
import lb.census.model.SourceIp;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Repository
public class SourceIpDaoImpl extends BaseDaoImpl implements SourceIpDao {

    @Override
    public List<SourceIp> getAllSourceIPs(int lastDays) {
        TypedQuery<SourceIp> typedQuery = getEntityManager()
                .createQuery(
                        "SELECT s FROM SourceIp s WHERE s.lastUsed >= :d ORDER BY s.userId, s.ip",
                        SourceIp.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -lastDays);
        typedQuery.setParameter("d", calendar.getTime());

        List<SourceIp> resultList = typedQuery.getResultList();

        // TODO: check if the following is necessary: is the list lazy?
        ArrayList<SourceIp> returnList = new ArrayList<>(resultList.size());
        returnList.addAll(resultList);
        return returnList;
    }

    @Transactional(readOnly = true)
    @Override
    public SourceIp getSourceIP(String userId, String ip, String subject) {
        TypedQuery<SourceIp> query = getEntityManager().createQuery(
                "SELECT s FROM SourceIp s WHERE s.userId = :userId AND s.ip = :ip AND s.subject = :s",
                SourceIp.class);
        query.setParameter("userId", userId);
        query.setParameter("ip", ip);
        query.setParameter("s", subject);
        List<SourceIp> resultList = query.getResultList();
        // there can be only one
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    @Transactional
    @Override
    public void save(SourceIp sourceIP) {
        EntityManager entityManager = getEntityManager();
        if (sourceIP.getId() != null) {
            entityManager.merge(sourceIP);
        } else {
            entityManager.persist(sourceIP);
        }
    }

    @Override
    public List<SourceIp> getSourceIPsFor(String userId, int lastDays) {
        TypedQuery<SourceIp> typedQuery = getEntityManager()
                .createQuery(
                        "SELECT s FROM SourceIp s WHERE s.lastUsed >= :d "
                                + " AND s.userId = :q"
                                + " ORDER BY s.userId, s.ip",
                        SourceIp.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -lastDays);
        typedQuery.setParameter("d", calendar.getTime());
        typedQuery.setParameter("q", userId);

        List<SourceIp> resultList = typedQuery.getResultList();

        // TODO: check if the following is necessary: is the list lazy?
        ArrayList<SourceIp> returnList = new ArrayList<>(resultList.size());
        returnList.addAll(resultList);
        return returnList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public int deleteSourceIPsOf(int lastDays) {
        Query query = getEntityManager().createQuery(
                "DELETE FROM SourceIp s WHERE s.lastUsed < :d");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -lastDays);
        query.setParameter("d", calendar.getTime());
        return query.executeUpdate();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Override
    public List<SourceIp> searchSourceIPs(String query, int lastDays) {
        TypedQuery<SourceIp> typedQuery = getEntityManager()
                .createQuery(
                        "SELECT s FROM SourceIp s WHERE s.lastUsed >= :d "
                                + " AND (s.userId like :q OR s.ip like :q)"
                                + " ORDER BY s.userId, s.ip",
                        SourceIp.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -lastDays);
        typedQuery.setParameter("d", calendar.getTime());
        typedQuery.setParameter("q", "%" + query + "%");

        List<SourceIp> resultList = typedQuery.getResultList();

        // TODO: check if the following is necessary: is the list lazy?
        ArrayList<SourceIp> returnList = new ArrayList<>(resultList.size());
        returnList.addAll(resultList);
        return returnList;
    }
}
