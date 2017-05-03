package lb.census.rest.retrieval;

import lb.census.record.scheduler.AccessLogRetriever;
import lb.census.record.scheduler.ScheduledRetrieval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private AccessLogRetriever accessLogRetriever;

    /**
     * Schedules a new retrieval operation. If the subject of the payload is *, a retrieval operation for each
     * known subject will be created.
     *
     * @param schedule
     */
    @RequestMapping(method = RequestMethod.POST)
    public void schedule(@RequestBody Schedule schedule) {
        accessLogRetriever.scheduleRetrieval(schedule.date, schedule.subject);
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
