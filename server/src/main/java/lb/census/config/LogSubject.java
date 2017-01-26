package lb.census.config;


import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import lb.census.record.filters.Filter;
import lb.census.record.log.LogRecordFactory;

/**
 * Created by phili on 20/12/2015.
 */
@XmlRootElement(name = "subject")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogSubject {

    @XmlAttribute(name = "id")
    private String id;
    @XmlAttribute(name = "name")
    private String name;
    @XmlElementWrapper(name = "sources")
    @XmlElement(name = "source")
    private List<LogSource> sources;
    @XmlElementRef
    private LogRecordFactory logPattern;
    @XmlElementWrapper(name = "filters")
    @XmlElementRef
    private List<Filter> filters;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LogSource> getSources() {
        return sources;
    }

    public void setSources(List<LogSource> sources) {
        this.sources = sources;
    }

    public LogRecordFactory getLogPattern() {
        return logPattern;
    }

    public void setLogPattern(LogRecordFactory logPattern) {
        this.logPattern = logPattern;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }
}
