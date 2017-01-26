package lb.census.api.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lb.census.config.CensusConfig;
import lb.census.dao.SubjectDao;

/**
 * @author phili
 * @since 23-Nov-2015
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class ConfigController {

    @Autowired
    private CensusConfig censusConfig;
    @Autowired
    private SubjectDao subjectDao;

    @RequestMapping(value = "/config", produces = "application/json")
    public CensusConfig getConfig() {
        return censusConfig;
    }

    @RequestMapping(value = "/subjects")
    public List<OneSubject> getSubjects() {
        List<OneSubject> oneSubjects = new ArrayList<>();
        subjectDao.getSubjects().stream().forEach(s -> {
            oneSubjects.add(new OneSubject(s.getId(), s.getName()));
        });
        return oneSubjects;
    }
}
