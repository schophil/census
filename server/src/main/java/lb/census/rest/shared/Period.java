package lb.census.rest.shared;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Possible period expressions:
 * <ul>
 * <li>yyyy-MM-dd+yyyy-MM-dd: 2 explicit given dates</li>
 * <li>-[days]: A number of days from yesterday</li>
 * <li>default: 30 days from yesterday</li>
 * </ul>
 * Created by philippeschottey on 03/03/2017.
 */
public interface Period {

    Logger LOGGER = LoggerFactory.getLogger(Period.class);

    static Period parse(String value) throws ParseException {
        if (isEmpty(value)) {
            return null;
        }

        LOGGER.trace("Trying to parse {}", value);
        value = value.trim().toLowerCase();
        if (value.equals("default")) {
            return new From(DateUtils.addDays(new Date(), -1), 30);
        }
        if (value.contains("+")) {
            String[] dates = value.split("\\+");
            if (dates == null || dates.length != 2) {
                throw new ParseException("Could not find 2 dates in range", value.indexOf("+"));
            }
            Date from = DateUtils.parseDate(dates[0], "yyyy-MM-dd");
            Date to = DateUtils.parseDate(dates[1], "yyyy-MM-dd");
            return new Range(from, to);
        }
        if (value.startsWith("-")) {

        }

        throw new ParseException("Unable to parse period", 0);
    }

    Date getFrom();

    Date getTo();
}
