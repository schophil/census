package lb.census.record;

import java.io.File;
import java.util.Date;
import java.util.List;

import lb.census.record.filters.Filter;
import lb.census.record.log.LogRecordFactory;

public class LogSet {

    private Date date;
    private List<File> logFiles;
    private LogRecordFactory logRecordFactory;
    private List<Filter> filters;

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public List<File> getLogFiles() {
        return logFiles;
    }

    public void setLogFiles(List<File> logFiles) {
        this.logFiles = logFiles;
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
}
