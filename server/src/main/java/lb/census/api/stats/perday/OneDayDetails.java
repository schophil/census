package lb.census.api.stats.perday;

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
