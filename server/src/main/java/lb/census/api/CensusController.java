package lb.census.api;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

/**
 * Created by philippe on 31/12/15.
 */
public abstract class CensusController {

    /**
     * 2015-12-23T00:00:00.000Z
     *
     * @param dateParameter
     * @return
     */
    protected Date toDate(String dateParameter) throws ParseException {
        return DateUtils.parseDate(dateParameter, "yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }
}
