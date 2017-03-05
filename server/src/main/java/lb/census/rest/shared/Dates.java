package lb.census.rest.shared;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by philippeschottey on 05/03/2017.
 */
public class Dates {

    public static Date toDate(String dateParameter) throws ParseException {
        return DateUtils.parseDate(dateParameter, "yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }
}
