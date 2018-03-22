package lb.census.record.recorders;

import static org.apache.commons.lang3.StringUtils.removeEnd;

import java.util.Date;

import lb.census.record.metrics.MetricsCalculator;
import lb.census.record.metrics.SubKeyMetricsCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lb.census.dao.ResourceDao;
import lb.census.model.DayStats;
import lb.census.model.Resource;
import lb.census.record.log.LogRecord;

/**
 * Created by philippe on 24/04/16.
 */
@Component
public class ResourceRecorder implements Recorder {

    private SubKeyMetricsCollector<SubKeyMetricsCollector<MetricsCalculator>> metricsByUserAndResource;
    private SubKeyMetricsCollector<MetricsCalculator> metricsByResource;

    @Autowired
    private ResourceDao resourceDao;

    @Override
    public void initialize() {
        metricsByResource = makeMetricsPerResource();

        metricsByUserAndResource = new SubKeyMetricsCollector<>(lr -> lr.getUserId(),
                ResourceRecorder::makeMetricsPerResource);
    }

    @Override
    public void record(LogRecord logRecord, RecorderContext recorderContext) {
        metricsByResource.add(logRecord);
        metricsByUserAndResource.add(logRecord);
    }

    @Override
    public void store(Date date, RecorderContext recorderContext) {
        DayStats dayStats = recorderContext.getCurrentDayStats();

        metricsByUserAndResource.getMetricsCollectors().entrySet().stream().forEach(entry -> {
            String userId = entry.getKey();
            entry.getValue().getMetricsCollectors().entrySet().stream().forEach(entry2 -> {
                Resource resource = new Resource();
                resource.setName("Path");
                resource.setDayStats(dayStats);
                resource.setTextValue(entry2.getKey());
                resource.setUserId(userId);

                // set the collected metrics
                entry2.getValue().update(resource);

                resourceDao.save(resource);
            });
        });

        metricsByResource.getMetricsCollectors().entrySet().stream().forEach(entry -> {
            Resource resource = new Resource();
            resource.setName("Path");
            resource.setDayStats(dayStats);
            resource.setTextValue(entry.getKey());

            // set the collected metrics
            entry.getValue().update(resource);

            resourceDao.save(resource);
        });

        // we can forget after storing
        metricsByUserAndResource = null;
        metricsByResource = null;
    }

    private static String getResource(LogRecord lr) {
        return lr.getMethod() + ":" + removeEnd(lr.getResource(), "/");
    }

    private static SubKeyMetricsCollector<MetricsCalculator> makeMetricsPerResource() {
        return new SubKeyMetricsCollector<>(ResourceRecorder::getResource, () -> new MetricsCalculator(2));
    }

    @Override
    public void forget(RecorderContext recorderContext) {
    }
}
