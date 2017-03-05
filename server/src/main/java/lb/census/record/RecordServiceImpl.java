package lb.census.record;

import lb.census.dao.SubjectDao;
import lb.census.model.Subject;
import lb.census.record.filters.Filter;
import lb.census.record.log.LogRecord;
import lb.census.record.log.LogRecordFactory;
import lb.census.record.recorders.Recorder;
import lb.census.record.recorders.RecorderContext;
import lb.census.utils.StreamCounter;
import lb.census.utils.StreamLogger;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.stream.Stream;

@Service
public class RecordServiceImpl implements RecordService {

    private static final Logger LOGGER = LoggerFactory.getLogger("CENSUS_RECORDERS");
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
    public RecorderContext record(LogSet logSet, Subject subject) {
        // Fix the date; make sure no time is specified
        logSet.setDate(DateUtils.truncate(logSet.getDate(), Calendar.DAY_OF_MONTH));
        // First make sure the subject is persisted
        assertSubject(subject);



        RecorderContext context = new RecorderContext(subject);
        initializeRecorders();
        try {
            // parse all the files
            LOGGER.info("Recording data for {}", subject);
            record(logSet, context);
            // store the recorded information
            LOGGER.info("Storing data for {}", subject);
            store(logSet.getDate(), context);
        } finally {
            // clean up recorders
            LOGGER.info("Clean-up recorders...");
            clean(context);
        }
        LOGGER.info("Done recording for {}!", subject);
        return context;
    }

    // Prepares the recorders for recording.
    private void initializeRecorders() {
        recorders.stream().forEach(recorder -> {
            recorder.initialize();
        });
    }

    // Cleans the recorders after storing.
    private void clean(RecorderContext recorderContext) {
        recorders.stream().forEach((recorder) -> {
            recorder.forget(recorderContext);
        });
    }

    // Records the data
    private void record(LogSet logSet, RecorderContext recorderContext) {
        LogRecordFactory logRecordFactory = logSet.getLogRecordFactory();
        for (Stream<String> stream : logSet.getLogData()) {
            LOGGER.info("Process stream {} ...", stream);
            recordOneStream(stream, logRecordFactory, logSet.getFilters(), recorderContext);
        }
    }

    // Instructs the recorders to store the recorded data.
    private void store(Date date, RecorderContext recorderContext) {
        recorders.stream().forEach((recorder) -> {
            recorder.store(date, recorderContext);
        });
    }

    // Records 1 log record.
    private void recordOneLine(LogRecord logRecord, RecorderContext recorderContext) {
        recorders.stream().forEach((recorder) -> {
            recorder.record(logRecord, recorderContext);
        });
    }

    // Checks if a given log record passes the filters.
    private boolean passesFilters(String line, List<Filter> filters) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }
        return filters.stream().allMatch(f -> f.passes(line));
    }

    private boolean passesFilters(LogRecord logRecord, List<Filter> filters) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }
        return filters.stream().allMatch(f -> f.passes(logRecord));
    }

    private void recordOneStream(Stream<String> stream, LogRecordFactory logRecordFactory, List<Filter> filters,
                                 RecorderContext recorderContext) {
        StreamCounter<String> totalLines = new StreamCounter();   // counts the total number of log lines
        StreamCounter<LogRecord> processed = new StreamCounter(); // counts the number of actually processed lines

        StreamLogger<String> preFilterLogger = new StreamLogger(LOGGER, "Pre filter");
        StreamLogger<String> preCreateLogger = new StreamLogger(LOGGER, "Pre create");
        StreamLogger<LogRecord> preProcessLogger = new StreamLogger(LOGGER, "Pre process");

        stream.map(totalLines::inspect)
                // first filtering
                .map(preFilterLogger::inspect)
                .filter(l -> passesFilters(l, filters))
                // create log records and remove null values and re-apply filters
                .map(preCreateLogger::inspect)
                .map(logRecordFactory::create)
                .filter(l -> l != null)
                .filter(l -> passesFilters(l, filters))
                // process the records
                .map(processed::inspect)
                .map(preProcessLogger::inspect)
                .forEach(l -> recordOneLine(l, recorderContext));

        LOGGER.info("Result for {}: recorded {}, filtered {}", stream, processed.getCount(),
                totalLines.getCount() - processed.getCount());
        recorderContext.addImported(processed.getCount());
        recorderContext.addFiltered(totalLines.getCount() - processed.getCount());
    }

    private void assertSubject(Subject subject) {
        getSubjectDao().save(subject);
    }
}
