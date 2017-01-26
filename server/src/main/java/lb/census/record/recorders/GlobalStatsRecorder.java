package lb.census.record.recorders;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lb.census.dao.DayStatsDao;
import lb.census.math.AverageCalculator;
import lb.census.math.OccurrenceCounter;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalStatsRecorder.class);
    @Autowired
    private DayStatsDao dayStatsDao;
    private AverageCalculator averageCalculator = new AverageCalculator(1000, 2);
    private OccurrenceCounter<String, String> occurrenceCounter = new OccurrenceCounter<String, String>();
    private double maxResponseTime = 0.0;
    private double minResponseTime = 0.0;

    public DayStatsDao getDayStatsDao() {
        return dayStatsDao;
    }

    public void setDayStatsDao(DayStatsDao dayStatsDao) {
        this.dayStatsDao = dayStatsDao;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void record(LogRecord logRecord, RecorderContext recorderContext) {
        String resultCode = logRecord.getResultCode();
        if ("200".equals(resultCode)) {
            resultCode = "ok";
        } else {
            resultCode = "nok";
        }

        // collect global result
        occurrenceCounter.register("global", resultCode);
        occurrenceCounter.register("global", "total");
        averageCalculator.add(logRecord.getResponseTime());
        if (maxResponseTime < logRecord.getResponseTime()) {
            maxResponseTime = logRecord.getResponseTime();
        } else if (minResponseTime == 0.0
                || minResponseTime > logRecord.getResponseTime()) {
            minResponseTime = logRecord.getResponseTime();
        }
    }

    @Override
    public void store(Date date, RecorderContext recorderContext) {
        String subjectId = recorderContext.getSubject().getId();
        // delete any existing record on this date
        LOGGER.debug("Create day stats for date {}", date);
        DayStats dayStats = dayStatsDao.getDayStats(date, subjectId);
        if (dayStats != null) {
            LOGGER.debug("Delete existing day stats");
            dayStatsDao.delete(dayStats);
        }

        // create a new entry
        dayStats = new DayStats();
        dayStats.setSubject(subjectId);
        dayStats.setDate(date);
        dayStats.setAverageResponseTime(averageCalculator.getCurrentAverage().doubleValue());
        dayStats.setMaxResponseTime(maxResponseTime);
        dayStats.setMinResponseTime(minResponseTime);
        dayStats.setTotalRequests(occurrenceCounter.getOccurrences("global", "total"));
        dayStats.setTotalRequestsInError(occurrenceCounter.getOccurrences("global", "nok"));

        dayStatsDao.save(dayStats);
        recorderContext.setCurrentDayStats(dayStats);
    }

    @Override
    public void forget(RecorderContext recorderContext) {
        clean();
    }

    private void clean() {
        averageCalculator = new AverageCalculator(1000, 2);
        occurrenceCounter = new OccurrenceCounter<String, String>();
        maxResponseTime = 0.0;
        minResponseTime = 0.0;
    }
}
