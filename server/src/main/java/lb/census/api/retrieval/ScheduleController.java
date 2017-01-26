package lb.census.api.retrieval;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lb.census.record.scheduler.AccessLogRetriever;
import lb.census.record.scheduler.ScheduledRetrieval;

/**
 * Created by phili on 21/12/2015.
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/retrieve")
public class ScheduleController {

    @Autowired
    private AccessLogRetriever accessLogRetriever;

    @RequestMapping(value = "/schedule", method = RequestMethod.POST)
    public void schedule(@RequestBody Schedule schedule) {
        accessLogRetriever.scheduleRetrieval(schedule.date, schedule.subject);
    }

    @RequestMapping(value = "/scheduled", method = RequestMethod.GET)
    public Collection<ScheduledRetrieval> pending() {
        return accessLogRetriever.getAllRetrievals();
    }
}
