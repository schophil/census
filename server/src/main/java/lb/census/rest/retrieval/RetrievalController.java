package lb.census.rest.retrieval;

import lb.census.config.CensusConfig;
import lb.census.config.LogSubject;
import lb.census.record.scheduler.AccessLogRetriever;
import lb.census.record.scheduler.ScheduledRetrieval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * Created by philippe on 27/04/2017.
 */
@RestController
@RequestMapping("/rest/retrievals")
public class RetrievalController {

    private static Logger LOGGER = LoggerFactory.getLogger(RetrievalController.class);

    @Autowired
    private CensusConfig censusConfig;
    @Autowired
    private AccessLogRetriever accessLogRetriever;

    /**
     * Schedules a new retrieval operation. If the subject of the payload is *, a retrieval operation for each
     * known subject will be created.
     *
     * @param schedule
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> schedule(@RequestBody Schedule schedule) {
        LogSubject logSubject = censusConfig.getSubjectWith(schedule.subject);
        if (logSubject == null) {
            return ResponseEntity.badRequest().body("Unknown subject " + schedule.subject);
        }
        accessLogRetriever.scheduleRetrieval(schedule.date, schedule.subject);
        return ResponseEntity.ok("ok");
    }

    /**
     * Lists the scheduled retrieval operations.
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Collection<ScheduledRetrieval> pending() {
        return accessLogRetriever.getAllRetrievals();
    }
}
