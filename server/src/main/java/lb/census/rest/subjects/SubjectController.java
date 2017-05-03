package lb.census.rest.subjects;

import lb.census.config.CensusConfig;
import lb.census.dao.SubjectDao;
import lb.census.model.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by philippeschottey on 01/03/2017.
 */
@RestController
@RequestMapping("/rest/subjects")
public class SubjectController {

    @Autowired
    private SubjectDao subjectDao;

    /**
     * Returns the list of available subjects.
     *
     * @return
     */
    @RequestMapping
    public ResponseEntity<List<OneSubject>> getSubjects() {
        List<OneSubject> subjects = subjectDao.getSubjects().stream()
                .map(s -> new OneSubject(s.getId(), s.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(subjects);
    }

    @RequestMapping(value = "/{id}")
    public ResponseEntity<OneSubject> getSubject(@PathVariable String id) {
        Subject subject = subjectDao.get(id);
        if (subject == null) {
            // return not found
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new OneSubject(subject.getId(), subject.getName()));
    }
}
