package lb.census.rest.subjects.stats;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by phili on 21/12/2015.
 */
public class OneDay {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
    public Date date;
    public Integer totalRequests;
    public Integer totalRequestsInError;
    public Double averageResponseTime;
    public Double maxResponseTime;
    public Double minResponseTime;
    public Integer totalUserIds;
    public Integer share;
}
