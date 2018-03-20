package lb.census.config;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import lb.census.record.filters.Invert;
import org.apache.commons.lang3.StringUtils;
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
        if (url == null) {
            LOGGER.warn("No config to load from, returning empty one!");
            return new CensusConfig();
        }
        try {
            JAXBContext context = JAXBContext.newInstance(CensusConfig.class, LogSource.class, LogSubject.class,
                    CsvLog.class, JhksStandardLog.class, ApacheLogFormat.class, PatternFilter.class, Invert.class);
            return (CensusConfig) context.createUnmarshaller().unmarshal(url);
        } catch (JAXBException e) {
            LOGGER.error("Error reading config from " + url + ", returning empty config!");
            LOGGER.error("Reading error: " + (StringUtils.isEmpty(e.getMessage()) ? e.getCause().getMessage() : e.getMessage()));
            return new CensusConfig();
        }
    }
}
