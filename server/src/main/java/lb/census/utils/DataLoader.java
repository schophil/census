package lb.census.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lb.census.record.LogSet;
import lb.census.record.RecordService;
import lb.census.record.log.LogRecordFactory;

/**
 *
 * @author psc
 */
public class DataLoader {

    private RecordService recordService;
    private List<String> resources;
    private LogRecordFactory logRecordFactory;

    public RecordService getRecordService() {
        return recordService;
    }

    public void setRecordService(RecordService recordService) {
        this.recordService = recordService;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    public LogRecordFactory getLogRecordFactory() {
        return logRecordFactory;
    }

    public void setLogRecordFactory(LogRecordFactory logRecordFactory) {
        this.logRecordFactory = logRecordFactory;
    }

    public void load() {
        List<File> files = new ArrayList<>();
        for (String resource : resources) {
            File input = new File(getClass().getResource(resource).getFile());
            files.add(input);
        }

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1);

        LogSet logSet = new LogSet();
        logSet.setDate(c.getTime());
        logSet.setLogRecordFactory(getLogRecordFactory());
        logSet.setLogFiles(files);

        recordService.recordFiles(logSet, null);
    }
}
