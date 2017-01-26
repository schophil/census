package lb.census.dao;

import java.util.List;

import lb.census.model.Subject;

/**
 *
 * @author phili
 * @since 23-Nov-2015
 */
public interface SubjectDao {

    List<Subject> getSubjects();

    void save(Subject subject);
}
