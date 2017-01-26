package lb.census.record.anomalies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lb.census.dao.AnomalyDao;
import lb.census.model.Anomaly;

public class AnomalyDetectorService {

    private static final Logger logger = LoggerFactory
            .getLogger(AnomalyDetectorService.class);
    private AnomalyDao anomalyDao;
    private List<AnomalyDetector> anomalyDetectors = new ArrayList<AnomalyDetector>();

    public void checkForAnomaliesOn(Date target) {
        logger.debug("Checking for anomalies for {}", target);
        List<Anomaly> allAnomalies = new ArrayList<Anomaly>();
        for (AnomalyDetector anomalyDetector : anomalyDetectors) {
            logger.trace("Running anomaly detector {}", anomalyDetector);
            Collection<Anomaly> newAnomalies = anomalyDetector.analyze(target);
            if (newAnomalies != null) {
                allAnomalies.addAll(newAnomalies);
            }
        }
        logger.trace("Detected and savig {} anomalies", allAnomalies.size());
        for (Anomaly anomaly : allAnomalies) {
            anomalyDao.save(anomaly);
        }
    }

    public AnomalyDao getAnomalyDao() {
        return anomalyDao;
    }

    public void setAnomalyDao(AnomalyDao anomalyDao) {
        this.anomalyDao = anomalyDao;
    }

    public List<AnomalyDetector> getAnomalyDetectors() {
        return anomalyDetectors;
    }

    public void setAnomalyDetectors(List<AnomalyDetector> anomalyDetectors) {
        this.anomalyDetectors = anomalyDetectors;
    }
}
