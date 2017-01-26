package lb.census.record.recorders;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lb.census.dao.SourceIpDao;
import lb.census.model.SourceIp;
import lb.census.record.log.LogRecord;

@Component
public class SourceIpRecorder implements Recorder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceIpRecorder.class);
    private final HashMap<String, HashSet<String>> collected = new HashMap<>();
    @Autowired
    private SourceIpDao sourceIpDao;
    private int daysToKeep = -1; // by default we keep the ip addresses

    @Override
    public void initialize() {
    }

    @Override
    public void record(LogRecord logRecord, RecorderContext recorderContext) {
        LOGGER.debug("Try to record {}", logRecord);
        // TODO: set pattern
        if ("-".equals(logRecord.getSourceIp())) {
            return;
        }
        LOGGER.debug("Recording source ip {} for {}", logRecord.getSourceIp(), logRecord.getUserId());
        String userId = logRecord.getUserId();
        HashSet<String> sourceIps = collected.get(userId);
        if (sourceIps == null) {
            sourceIps = new HashSet<>();
            collected.put(userId, sourceIps);
        }
        sourceIps.add(logRecord.getSourceIp());
    }

    @Override
    public void store(Date date, RecorderContext recorderContext) {
        LOGGER.debug("Storing recorded source ips for {} users", collected.size());
        String subject = recorderContext.getSubject().getId();
        for (String userId : collected.keySet()) {
            HashSet<String> recordedIPs = collected.get(userId);
            for (String ip : recordedIPs) {
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
            }
        }

        if (daysToKeep > -1) {
            int deleted = sourceIpDao.deleteSourceIPsOf(daysToKeep);
            LOGGER.debug("Deleted {} ips", deleted);
        }
    }

    @Override
    public void forget(RecorderContext recorderContext) {
        collected.clear();
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
