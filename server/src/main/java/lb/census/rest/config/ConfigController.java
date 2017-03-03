package lb.census.rest.config;

import java.util.ArrayList;
import java.util.List;

import lb.census.rest.subjects.OneSubject;
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
@RestController
@RequestMapping("/rest")
public class ConfigController {

    @Autowired
    private CensusConfig censusConfig;
    @Autowired
    private SubjectDao subjectDao;

    @RequestMapping(value = "/config")
    public CensusConfig getConfig() {
        return censusConfig;
    }
}
