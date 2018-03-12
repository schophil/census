package lb.census.record.scheduler;

import lb.census.config.CensusConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AutoRetrieval {

    @Autowired
    private AccessLogRetriever accessLogRetriever;
    @Autowired
    private CensusConfig config;

    @Scheduled(cron = "0 0 3 * * ?")
    public void retrieve() {
        if (config.isAutoRetrieve()) {
            accessLogRetriever.scheduleRetrievalForYesterday();
        }
    }
}
