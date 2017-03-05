package lb.census.record;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import lb.census.record.filters.Filter;
import lb.census.record.log.LogRecordFactory;

public abstract class LogSet {

    private Date date;
    private LogRecordFactory logRecordFactory;
    private List<Filter> filters;

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public LogRecordFactory getLogRecordFactory() {
        return logRecordFactory;
    }

    public void setLogRecordFactory(LogRecordFactory logRecordFactory) {
        this.logRecordFactory = logRecordFactory;
    }

    public abstract List<Stream<String>> getLogData();
}
