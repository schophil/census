package lb.census.rest.subjects.stats;

import lb.census.dao.*;
import lb.census.model.CategoryFilter;
import lb.census.rest.shared.Dates;
import lb.census.rest.shared.Period;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by philippeschottey on 01/03/2017.
 */
@RestController
@RequestMapping("/rest/subjects/{subject}/stats")
public class StatsPerDayController {

    private static Logger LOGGER = LoggerFactory.getLogger(StatsPerDayController.class);

    @Autowired
    private StatsPerDayService statsPerDayService;

    @Autowired
    private DayDetailsService dayDetailsService;


    /**
     * Lists statistics per day for a given period and subject. Optionally a filter on the category of the user(s)
     * can be given. The latter will cause the result to contain statistics that only take into account users
     * matching the category filter.
     * <p>
     * The period is also optional. By default yesterday - 30 days will be the period.
     *
     * @param subject
     * @param category
     * @param period
     * @return
     */
    @RequestMapping("/days")
    public ResponseEntity<List<OneDay>> list(@PathVariable String subject,
                                             @RequestParam(required = false) String category,
                                             @RequestParam(defaultValue = "default") String period) {
        Period aPeriod = null;
        if (!StringUtils.isEmpty(period)) {
            try {
                aPeriod = Period.parse(period);
            } catch (ParseException e) {
                LOGGER.error("Error parsing period {}", period);
                return ResponseEntity.badRequest().build();
            }
        }
        if (aPeriod == null) {
            LOGGER.error("Period is still null, that's not normal");
        }

        List<CategoryFilter> categoryFilters = null;
        if (!StringUtils.isEmpty(category)) {
            categoryFilters = CategoryFilter.parse(category);
        }

        List<OneDay> days = null;
        if (categoryFilters == null || categoryFilters.isEmpty()) {
            days = statsPerDayService.list(aPeriod, subject);
        } else {
            days = statsPerDayService.list(aPeriod, categoryFilters, subject);
        }

        return ResponseEntity.ok(days);
    }

    @RequestMapping("/days/{date}")
    public ResponseEntity<OneDayDetails> getDetails(@PathVariable String subject, @PathVariable String date) {
        LOGGER.trace("Requesting the day details for {} on {}", subject, date);
        Date requestedDate = null;
        try {
            requestedDate = Dates.toDate(date);
            LOGGER.trace("Parsed {} to {}", date, requestedDate);
        } catch (ParseException e) {
            LOGGER.error("Unable to parse date " + date, e);
            return ResponseEntity.badRequest().build();
        }

        OneDayDetails oneDayDetails = dayDetailsService.getDetails(subject, requestedDate);

        return ResponseEntity.ok(oneDayDetails);
    }

    @RequestMapping("/days/{date}/{user}")
    public ResponseEntity<OneUserDetails> getUserDetails(@PathVariable String subject, @PathVariable String date, @PathVariable String user) {
        LOGGER.trace("Requesting the day details for {} on {}", subject, date);
        Date requestedDate = null;
        try {
            requestedDate = Dates.toDate(date);
            LOGGER.trace("Parsed {} to {}", date, requestedDate);
        } catch (ParseException e) {
            LOGGER.error("Unable to parse date " + date, e);
            return ResponseEntity.badRequest().build();
        }

        OneUserDetails oneUserDetails = dayDetailsService.getUserDetails(subject, requestedDate, user);
        return ResponseEntity.ok(oneUserDetails);
    }
}
