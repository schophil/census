package lb.census.config;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lb.census.record.filters.PatternFilter;
import lb.census.record.log.ApacheLogFormat;
import lb.census.record.log.CsvLog;
import lb.census.record.log.JhksStandardLog;

/**
 * Created by phili on 20/12/2015.
 */
@Configuration
public class CensusConfigFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CensusConfigFactory.class);
    @Autowired
    private Resources resources;

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    @Bean
    public CensusConfig censusConfig() {
        URL configResource = resources.getCensusConfig();
        LOGGER.info("Reading config from {}", configResource);
        return loadFrom(configResource);
    }

    public CensusConfig loadFrom(URL url) {
        try {
            JAXBContext context = JAXBContext.newInstance(CensusConfig.class, LogSource.class, LogSubject.class,
                    CsvLog.class, JhksStandardLog.class, ApacheLogFormat.class, PatternFilter.class);
            return (CensusConfig) context.createUnmarshaller().unmarshal(url);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
