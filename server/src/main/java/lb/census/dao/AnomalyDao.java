package lb.census.dao;

import java.util.List;

import lb.census.model.Anomaly;

public interface AnomalyDao {

    void save(Anomaly anomaly);

    List<Anomaly> getAllAnomalies();
}
