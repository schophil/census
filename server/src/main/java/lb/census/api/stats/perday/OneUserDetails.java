package lb.census.api.stats.perday;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by philippe on 19/05/16.
 */
public class OneUserDetails {

    public String userId;
    public String userName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
    public Date date;

    public List<OneHour> activityPerHour;
    public List<OneResource> popularResources;
}
