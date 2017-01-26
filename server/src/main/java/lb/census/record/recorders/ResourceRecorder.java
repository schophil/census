package lb.census.record.recorders;

import static org.apache.commons.lang3.StringUtils.removeEnd;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lb.census.dao.ResourceDao;
import lb.census.math.OccurrenceCounter;
import lb.census.model.DayStats;
import lb.census.model.Resource;
import lb.census.record.log.LogRecord;

/**
 * Created by philippe on 24/04/16.
 */
@Component
public class ResourceRecorder implements Recorder {

    private final OccurrenceCounter<String, String> occurrenceCounter = new OccurrenceCounter<>();
    @Autowired
    private ResourceDao resourceDao;

    @Override
    public void initialize() {
    }

    @Override
    public void record(LogRecord logRecord, RecorderContext recorderContext) {
        // remove trailing forward slashes
        String resource = removeEnd(logRecord.getResource(), "/");

        // we count hits globally
        occurrenceCounter.register("global", resource);
        // and per user
        occurrenceCounter.register(logRecord.getUserId(), resource);
    }

    @Override
    public void store(Date date, RecorderContext recorderContext) {
        DayStats dayStats = recorderContext.getCurrentDayStats();

        // first store the globals
        occurrenceCounter.forEach((groupId, value, hits) -> {
            Resource resource = new Resource();
            resource.setName("Path");
            resource.setDayStats(dayStats);
            resource.setHits(hits);
            resource.setTextValue(value);
            if (!groupId.equals("global")) {
                resource.setUserId(groupId);
            }
            resourceDao.save(resource);
        });
    }

    @Override
    public void forget(RecorderContext recorderContext) {
        occurrenceCounter.clear();
    }
}
