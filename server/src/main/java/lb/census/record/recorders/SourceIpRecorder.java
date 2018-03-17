package lb.census.record.recorders;

import lb.census.dao.SourceIpDao;
import lb.census.model.SourceIp;
import lb.census.record.log.LogRecord;
import lb.census.record.metrics.SourceIpCollector;
import lb.census.record.metrics.SubKeyMetricsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SourceIpRecorder implements Recorder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceIpRecorder.class);
    @Autowired
    private SourceIpDao sourceIpDao;
    private int daysToKeep = -1; // by default we keep the ip addresses
    private SubKeyMetricsCollector<SourceIpCollector> collector;

    @Override
    public void initialize() {
        collector = new SubKeyMetricsCollector<>(
                lr -> lr.getUserId(),
                () -> new SourceIpCollector()
        );
    }

    @Override
    public void record(LogRecord logRecord, RecorderContext recorderContext) {
        LOGGER.debug("Try to record {}", logRecord);
        // TODO: set pattern
        if ("-".equals(logRecord.getSourceIp())) {
            return;
        }
        LOGGER.debug("Recording source ip {} for {}", logRecord.getSourceIp(), logRecord.getUserId());
        collector.add(logRecord);
    }

    @Override
    public void store(Date date, RecorderContext recorderContext) {
        String subject = recorderContext.getSubject().getId();

        collector.getMetricsCollectors().entrySet().stream().forEach( entry -> {
            String userId = entry.getKey();

            entry.getValue().getIps().stream().forEach(ip -> {
                SourceIp sourceIP = sourceIpDao.getSourceIP(userId, ip, subject);
                if (sourceIP == null) {
                    // create new one
                    sourceIP = new SourceIp();
                    sourceIP.setIp(ip);
                    sourceIP.setUserId(userId);
                    sourceIP.setLastUsed(date);
                    sourceIP.setSubject(subject);
                    sourceIpDao.save(sourceIP);
                } else {
                    sourceIP.setLastUsed(date);
                    sourceIpDao.save(sourceIP);
                }
            });

        });

        collector = null;

        if (daysToKeep > -1) {
            int deleted = sourceIpDao.deleteSourceIPsOf(daysToKeep);
            LOGGER.debug("Deleted {} ips", deleted);
        }
    }

    @Override
    public void forget(RecorderContext recorderContext) {
    }

    public SourceIpDao getSourceIpDao() {
        return sourceIpDao;
    }

    public void setSourceIpDao(SourceIpDao sourceIPDao) {
        this.sourceIpDao = sourceIPDao;
    }

    public int getDaysToKeep() {
        return daysToKeep;
    }

    public void setDaysToKeep(int daysToKeep) {
        this.daysToKeep = daysToKeep;
    }
}
