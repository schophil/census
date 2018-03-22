package lb.census.rest.subjects.stats;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

/**
 * The user activity for one day.
 */
public class OneUserDetails {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
    public Date date;
    public String userId;
    public String userName;
    public List<OneHour> activityPerHour;
    public List<OneResource> popularResources;
}
