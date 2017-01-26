package lb.census.dao.jpa;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lb.census.dao.UserDao;
import lb.census.model.User;

@Repository
public class UserDaoImpl extends BaseDaoImpl implements UserDao {

    @Override
    @Transactional(readOnly = true)
    public User getUser(String userId) {
        return getEntityManager().find(User.class, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers(String subject) {
        // select all users
        TypedQuery<User> query = getEntityManager().createQuery("SELECT u FROM User u WHERE u.subject = :s OR u.subject = 'ALL'", User.class);
        query.setParameter("s", subject);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersFor(Date date, String subject) {
        String ql = "SELECT u FROM User u WHERE "
                + " and e.subject = :s "
                + "(u.dateBegin <= :date OR u.dateBegin is null) and (u.dateEnd >= :date OR u.dateEnd is null)";
        // select all users
        TypedQuery<User> query = getEntityManager().createQuery(ql, User.class);
        query.setParameter("date", date);
        query.setParameter("s", subject);
        return query.getResultList();
    }

    @Transactional
    @Override
    public User save(User user) {
        if (user.getUserId() != null) {
            return getEntityManager().merge(user);
        } else {
            getEntityManager().persist(user);
            return user;
        }
    }
}
