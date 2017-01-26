package lb.census.dao.jpa;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lb.census.dao.SubjectDao;
import lb.census.model.Subject;

/**
 *
 * @author phili
 * @since 23-Nov-2015
 */
@Repository
public class SubjectDaoImpl extends BaseDaoImpl implements SubjectDao {

    @Override
    @Transactional(readOnly = true)
    public List<Subject> getSubjects() {
        return getEntityManager().createQuery("SELECT s FROM Subject s", Subject.class).getResultList();
    }

    @Override
    public void save(Subject subject) {
        Subject persisted = getEntityManager().find(Subject.class, subject.getId());
        if (persisted == null) {
            getEntityManager().persist(subject);
        } else {
            getEntityManager().merge(subject);
        }
    }
}
