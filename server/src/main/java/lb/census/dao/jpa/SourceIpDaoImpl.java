package lb.census.dao.jpa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lb.census.dao.SourceIpDao;
import lb.census.model.SourceIp;

@Repository
public class SourceIpDaoImpl extends BaseDaoImpl implements SourceIpDao {

    @Transactional(readOnly = true)
    @Override
    public List<SourceIp> getSourceIPsFor(String userId, int lastDays, String subject) {
        TypedQuery<SourceIp> query = getEntityManager()
                .createQuery(
                        "SELECT s FROM SourceIp s WHERE s.subject = :s and s.userId = :u AND s.lastUsed >= :d",
                        SourceIp.class);
        query.setParameter("u", userId);
        query.setParameter("s", subject);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -lastDays);
        query.setParameter("d", calendar.getTime());

        List<SourceIp> resultList = query.getResultList();

        // TODO: check if the following is necessary: is the list lazy?
        ArrayList<SourceIp> returnList = new ArrayList<>(resultList.size());
        returnList.addAll(resultList);
        return returnList;
    }

    @Transactional(readOnly = true)
    @Override
    public List<SourceIp> getSourceIPsFor(String userId, String subject) {
        TypedQuery<SourceIp> query = getEntityManager().createQuery(
                "SELECT s FROM SourceIp s WHERE s.subject = :s and s.userId = :u", SourceIp.class);
        query.setParameter("u", userId);
        query.setParameter("s", subject);
        List<SourceIp> resultList = query.getResultList();
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

    @Transactional(readOnly = true)
    @Override
    public List<SourceIp> getSourceIPsFor(int lastDays, String subject) {
        TypedQuery<SourceIp> query = getEntityManager()
                .createQuery(
                        "SELECT s FROM SourceIp s WHERE s.subject = :s and s.lastUsed >= :d ORDER BY s.userId, s.ip",
                        SourceIp.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -lastDays);
        query.setParameter("d", calendar.getTime());
        query.setParameter("s", subject);

        List<SourceIp> resultList = query.getResultList();

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
}
