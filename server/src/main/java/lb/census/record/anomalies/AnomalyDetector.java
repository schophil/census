package lb.census.record.anomalies;

import java.util.Collection;
import java.util.Date;

import lb.census.model.Anomaly;

public interface AnomalyDetector {

    /**
     *
     * @param date The date to analyze
     * @return
     */
    Collection<Anomaly> analyze(Date date);
}
