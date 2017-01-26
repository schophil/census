package lb.census.record;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lb.census.dao.SubjectDao;
import lb.census.model.Subject;
import lb.census.record.filters.Filter;
import lb.census.record.log.LogRecord;
import lb.census.record.log.LogRecordFactory;
import lb.census.record.recorders.Recorder;
import lb.census.record.recorders.RecorderContext;

@Service
public class RecordServiceImpl implements RecordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordServiceImpl.class);
    @Autowired
    private List<Recorder> recorders;
    @Autowired
    private SubjectDao subjectDao;

    public SubjectDao getSubjectDao() {
        return subjectDao;
    }

    public void setSubjectDao(SubjectDao subjectDao) {
        this.subjectDao = subjectDao;
    }

    public List<Recorder> getRecorders() {
        return recorders;
    }

    public void setRecorders(List<Recorder> recorders) {
        this.recorders = recorders;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void recordFiles(LogSet logSet, Subject subject) {
        // Fix the date; make sure no time is specified
        logSet.setDate(DateUtils.truncate(logSet.getDate(), Calendar.DAY_OF_MONTH));
        // First make sure the subject is persisted
        assertSubject(subject);

        RecorderContext context = new RecorderContext(subject);
        initializeRecorders();
        try {
            // parse all the files
            LOGGER.info("Recording the information with the recorders...");
            record(logSet, context);
            // store the recorded information
            LOGGER.info("Storing the information...");
            store(logSet.getDate(), context);
        } finally {
            // clean up recorders
            LOGGER.info("Clean-up recorders...");
            clean(context);
        }
        LOGGER.info("Done recording!");
    }

    private void initializeRecorders() {
        recorders.stream().forEach(recorder -> {
            recorder.initialize();
        });
    }

    private void clean(RecorderContext recorderContext) {
        recorders.stream().forEach((recorder) -> {
            recorder.forget(recorderContext);
        });
    }

    private void record(LogSet logSet, RecorderContext recorderContext) {
        LogRecordFactory logRecordFactory = logSet.getLogRecordFactory();
        for (File logFile : logSet.getLogFiles()) {
            LOGGER.info("Process file {} ...", logFile);
            try {
                recordOneFile(logFile, logRecordFactory, logSet.getFilters(), recorderContext);
            } catch (IOException e) {
                LOGGER.error("Recording failed: " + e.getMessage());
            }
        }
    }

    private void store(Date date, RecorderContext recorderContext) {
        recorders.stream().forEach((recorder) -> {
            recorder.store(date, recorderContext);
        });
    }

    private void recordOneLine(LogRecord logRecord, RecorderContext recorderContext) {
        recorders.stream().forEach((recorder) -> {
            recorder.record(logRecord, recorderContext);
        });
    }

    private boolean passesFilters(String line, List<Filter> filters) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }
        for (Filter filter : filters) {
            if (!filter.passes(line)) {
                LOGGER.debug("Line '{}' blocked by filter {}", line, filter);
                return false;
            }
        }
        return true;
    }

    private boolean passesFilters(LogRecord logRecord, List<Filter> filters) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }
        for (Filter filter : filters) {
            if (!filter.passes(logRecord)) {
                LOGGER.debug("Log record {} blocked by filter {}", logRecord, filter);
                return false;
            }
        }
        return true;
    }

    private void recordOneFile(File logFile, LogRecordFactory logRecordFactory, List<Filter> filters, RecorderContext recorderContext) throws IOException {
        try (FileInputStream fin = new FileInputStream(logFile);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fin))) {
            int recorded = 0;
            int filtered = 0;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!passesFilters(line, filters)) {
                    filtered++;
                    continue;
                }
                LogRecord logRecord = logRecordFactory.create(line);
                if (logRecord != null) {
                    if (!passesFilters(logRecord, filters)) {
                        filtered++;
                        continue;
                    }
                    recordOneLine(logRecord, recorderContext);
                    recorded++;
                }
            }

            LOGGER.info("Result for {}: recorded {}, filtered {}", logFile, recorded, filtered);
        }
    }

    private void assertSubject(Subject subject) {
        getSubjectDao().save(subject);
    }
}
