package lb.census.rest.subjects.stats;

import lb.census.api.stats.perday.OneHour;
import lb.census.api.stats.perday.OneResource;
import lb.census.api.stats.perday.OneUser;
import lb.census.rest.subjects.stats.OneDay;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by philippe on 30/12/15.
 */
public class OneDayDetails extends OneDay {

    public List<OneUser> recordedUsers;
    public List<OneUser> missingUsers;
    public List<OneHour> activityPerHour;
    public List<OneResource> popularResources;

}
