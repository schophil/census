package lb.census.api.sources;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by philippe on 21/04/16.
 */
public class OneSourceIp {

    public String ip;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
    public Date lastUsed;
}
