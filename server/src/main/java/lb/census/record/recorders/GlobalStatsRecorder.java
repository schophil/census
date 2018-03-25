package lb.census.record.recorders;

import java.util.Date;

import lb.census.record.metrics.MetricsCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lb.census.dao.DayStatsDao;
import lb.census.model.DayStats;
import lb.census.record.log.LogRecord;

/**
 * Records the global statistics. This one should always be first since it create a DayStats object referenced
 * by almost all other model objects.
 *
 * @author psc
 */
@Component
@Order(value = 2)
public class GlobalStatsRecorder implements Recorder {

    private static final Logger LOGGER = LoggerFactory.getLogger("CENSUS_RECORDERS");
    @Autowired
    private DayStatsDao dayStatsDao;
    private MetricsCalculator metricsCalculator;

    public DayStatsDao getDayStatsDao() {
        return dayStatsDao;
    }

    public void setDayStatsDao(DayStatsDao dayStatsDao) {
        this.dayStatsDao = dayStatsDao;
    }

    @Override
    public void initialize() {
        metricsCalculator = new MetricsCalculator(2);
    }

    @Override
    public void record(LogRecord logRecord, RecorderContext recorderContext) {
        metricsCalculator.add(logRecord);
    }

    @Override
    public void store(Date date, RecorderContext recorderContext) {
        String subjectId = recorderContext.getSubject().getId();
        // delete any existing record on this date
        LOGGER.debug("Create day stats for date {}", date);
        DayStats dayStats = dayStatsDao.getDayStats(date, subjectId);
        if (dayStats != null) {
            LOGGER.info("Delete existing day stats {}", dayStats);
            dayStatsDao.delete(dayStats);
        }

        // create a new entry
        dayStats = new DayStats();
        dayStats.setSubject(subjectId);
        dayStats.setDate(date);

        // set the collected metrics
        metricsCalculator.update(dayStats);

        dayStatsDao.save(dayStats);
        recorderContext.setCurrentDayStats(dayStats);

        metricsCalculator = null;
    }

    @Override
    public void forget(RecorderContext recorderContext) {
    }
}
