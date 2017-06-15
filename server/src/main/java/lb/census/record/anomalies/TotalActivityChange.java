package lb.census.record.anomalies;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import lb.census.dao.DayStatsDao;
import lb.census.math.AverageCalculator;
import lb.census.math.PercentageCalculator;
import lb.census.model.Anomaly;
import lb.census.model.DayStats;

/**
 * <p>
 * Looks at the last X days and generates an anomaly if the total number of
 * request is more than X % off (higher or lower).
 * </p>
 *
 * @author Philippe
 * @since 4-sep.-2014
 */
public class TotalActivityChange implements AnomalyDetector {

    private static Logger LOGGER = LoggerFactory.getLogger(TotalActivityChange.class);

    @Autowired
    private DayStatsDao dayStatsDao;
    private double allowedDeviation = 10.0;
    private int totalDays = 3;

    public double getAllowedDeviation() {
        return allowedDeviation;
    }

    public void setAllowedDeviation(double allowedDeviation) {
        this.allowedDeviation = allowedDeviation;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public DayStatsDao getDayStatsDao() {
        return dayStatsDao;
    }

    public void setDayStatsDao(DayStatsDao dayStatsDao) {
        this.dayStatsDao = dayStatsDao;
    }

    @Override
    public Collection<Anomaly> analyze(Date date) {
        BigDecimal average = getAverage(date, totalDays);
        BigDecimal current = getCurrent(date);

        PercentageCalculator percentageCalculator = new PercentageCalculator();
        double realDeviation = percentageCalculator.forDifference(average, current);
        LOGGER.trace("Average: {}; current: {}; deviation: {}", average, current, realDeviation);
        return realDeviation > allowedDeviation ? createAnomaly(date, realDeviation) : null;
    }

    private Collection<Anomaly> createAnomaly(Date date, double percentage) {
        List<Anomaly> list = new ArrayList<>(1);
        Anomaly anomaly = new Anomaly();
        anomaly.setDate(date);
        anomaly.setDescription("Total requests deviation of " + percentage
                + " is higher than allowed deviation of " + allowedDeviation
                + ".");
        list.add(anomaly);
        return list;
    }

    private BigDecimal getCurrent(Date date) {
        DayStats dayStats = dayStatsDao.getDayStats(date, null);
        return new BigDecimal(dayStats.getTotalRequests());
    }

    private BigDecimal getAverage(Date from, int days) {
        AverageCalculator averageCalculator = AverageCalculator.create(2);

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(from);

        for (; days > 0; days--) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            DayStats dayStats = dayStatsDao.getDayStats(calendar.getTime(), null);
            if (dayStats != null) {
                averageCalculator.add(dayStats.getTotalRequests());
            }
        }

        return averageCalculator.getCurrentAverage();
    }
}
