package lb.census.record.scheduler;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import lb.census.record.FilesLogSet;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileUtil;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lb.census.config.CensusConfig;
import lb.census.config.LogSource;
import lb.census.config.LogSubject;
import lb.census.model.Subject;
import lb.census.record.RecordService;
import lb.census.record.anomalies.AnomalyDetectorService;
import lb.census.record.log.LogRecordFactory;

/**
 * Class responsible for retrieving and processing log files. Currently this
 * class has 2 tasks:
 * <ul>
 * <li>Launching a thread that runs the retrieval automatically each day.</li>
 * <li>Allow scheduling manual retrievals. These manual retrievals will also be
 * handled by the running thread.</li>
 * </ul>
 *
 * @author psc
 */
@Component
public class AccessLogRetriever implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger("CENSUS_RETRIEVER");
    private static final int RETRIEVED = 1;
    private static final int WAITING_FOR_RETRIEVAL = 2;
    private boolean keepRunning;
    private int status = WAITING_FOR_RETRIEVAL;
    @Autowired
    private CensusConfig censusConfig;
    // milliseconds
    private long checkInterval = 3 * 1000;
    // the time at which to retrieve automatically
    @Autowired
    private RecordService recordService;
    private AnomalyDetectorService anomalyDetectorService;
    // the list of scheduled retrievals
    private final List<ScheduledRetrieval> scheduled = new ArrayList<>();
    // the list of processed retrievals
    private List<ScheduledRetrieval> processed = new ArrayList<>();

    public AnomalyDetectorService getAnomalyDetectorService() {
        return anomalyDetectorService;
    }

    public CensusConfig getCensusConfig() {
        return censusConfig;
    }

    public void setCensusConfig(CensusConfig censusConfig) {
        this.censusConfig = censusConfig;
    }

    public void setAnomalyDetectorService(AnomalyDetectorService anomalyDetectorService) {
        this.anomalyDetectorService = anomalyDetectorService;
    }

    public RecordService getRecordService() {
        return recordService;
    }

    public void setRecordService(RecordService recordService) {
        this.recordService = recordService;
    }

    public boolean isKeepRunning() {
        return keepRunning;
    }

    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }

    public long getCheckInterval() {
        return checkInterval;
    }

    public void setCheckInterval(long checkInterval) {
        this.checkInterval = checkInterval;
    }

    @PostConstruct
    public void startup() {
        if (!censusConfig.isStartRetrieve()) {
            LOGGER.warn("Retrieval is deactivated!");
            return;
        }
        if (!censusConfig.isAutoRetrieve()) {
            LOGGER.warn("Auto retrieval is deactivated!");
        }
        setKeepRunning(true);
        Thread thread = new Thread(this);
        LOGGER.info("Starting retrieval thread");
        thread.start();
    }

    @PreDestroy
    public void shutdown() {
        LOGGER.info("Stopping retrieval thread");
        setKeepRunning(false);
        keepRunning = false;
    }

    public List<ScheduledRetrieval> getScheduledRetrievals() {
        List<ScheduledRetrieval> list = new ArrayList<>();
        list.addAll(scheduled);
        return list;
    }

    public List<ScheduledRetrieval> getAllRetrievals() {
        List<ScheduledRetrieval> list = getScheduledRetrievals();
        list.addAll(processed);
        return list;
    }

    @Override
    public void run() {
        sleep();

        while (keepRunning) {

            if (censusConfig != null) {
                if (censusConfig.isAutoRetrieve()) {
                    // automatic retrieval
                    int retrievalHour = censusConfig.getRetrievalHour();
                    Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    if (hour == retrievalHour && status == WAITING_FOR_RETRIEVAL) {
                        scheduleRetrievalForYesterday();
                        status = RETRIEVED;
                    } else if (hour > retrievalHour && status == RETRIEVED) {
                        status = WAITING_FOR_RETRIEVAL;
                    }
                }
                // check scheduled retrievals
                processScheduled();
                cleanProcessed();
            }

            sleep();
        }
    }

    /**
     * Schedules a retrieval for a specific date.
     *
     * @param target    The date to retrieve the logs for
     * @param subjectId The subject for which to retrieve the logs
     */
    public void scheduleRetrieval(Calendar target, String subjectId) {
        if (subjectId.equals("*")) {
            censusConfig.getSubjects().forEach(ls -> scheduled.add(new ScheduledRetrieval(target.getTime(), ls)));
        } else {
            LogSubject logSubject = censusConfig.getSubjectWith(subjectId);
            if (logSubject != null) {
                scheduled.add(new ScheduledRetrieval(target.getTime(), logSubject));
            }
        }
    }

    /**
     * Schedules the retrieval for yesterday.
     */
    public void scheduleRetrievalForYesterday() {
        // get yesterday
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        scheduleRetrieval(calendar, "*");
    }

    private void processScheduled() {
        // create local copy
        List<ScheduledRetrieval> localScheduled = getScheduledRetrievals();
        if (localScheduled.isEmpty()) {
            LOGGER.trace("No scheduled retrievals");
            return;
        }

        // processing
        LOGGER.info("Processing scheduled {} retrievals", localScheduled.size());
        for (ScheduledRetrieval scheduledRetrieval : localScheduled) {
            process(scheduledRetrieval);
            scheduled.remove(scheduledRetrieval);
            processed.add(0, scheduledRetrieval);
        }
    }

    private void cleanProcessed() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date limitDate = calendar.getTime();
        processed = processed
                .stream()
                .filter(sr -> !sr.isOlderThan(limitDate))
                .collect(Collectors.toList());
    }

    private void process(ScheduledRetrieval scheduledRetrieval) {
        LOGGER.info("Processing {}", scheduledRetrieval);
        scheduledRetrieval.start();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(scheduledRetrieval.getTarget());
        scheduledRetrieval.stop(retrieveAndRecord(calendar, scheduledRetrieval.getLogSubject()));
        // check for anomalies
        if (anomalyDetectorService != null) {
            anomalyDetectorService.checkForAnomaliesOn(calendar.getTime());
        }
    }

    private boolean retrieveAndRecord(Calendar calendar, LogSubject logSubject) {
        List<File> retrievedFiles = null;
        try {
            // retrieve
            retrievedFiles = retrieveAccessLogFiles(calendar, logSubject);
            LOGGER.info("Retrieved {} files", retrievedFiles.size());

            // record
            if (!retrievedFiles.isEmpty()) {
                FilesLogSet logSet = new FilesLogSet();
                logSet.setDate(calendar.getTime());
                logSet.setLogFiles(retrievedFiles);
                logSet.setLogRecordFactory(getLogRecordFactory(logSubject));
                logSet.setFilters(logSubject.getFilters());

                getRecordService().record(logSet, asModel(logSubject));
            }

            return true;
        } catch (FileSystemException e) {
            LOGGER.error("Retrieve failed for {}", logSubject);
            LOGGER.error("Retrieve failed", e);
            return false;
        } catch (Throwable t) {
            LOGGER.error("Retrieve failed for {}", logSubject);
            LOGGER.error("Retrieve failed", t);
            return false;
        } finally {
            // clean up retrieved files
            cleanRetrievedFiles(retrievedFiles);
        }
    }

    private void cleanRetrievedFiles(List<File> files) {
        if (files == null || files.isEmpty()) {
            return;
        }

        for (File file : files) {
            LOGGER.debug("Deleting file {}", file);
            file.delete();
        }
    }

    private List<File> retrieveAccessLogFiles(Calendar calendar, LogSubject logSubject) throws IOException {
        // configure ssh
        FileSystemOptions fsOptions = new FileSystemOptions();
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(fsOptions, "no");
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(fsOptions, false);

        List<File> retrievedFiles = new ArrayList<>(logSubject.getSources().size());
        FileSystemManager fsManager = VFS.getManager();
        int fileIndex = 1;

        for (LogSource logSource : logSubject.getSources()) {
            String url = logSource.getConcreteUrl(calendar);
            LOGGER.info("Retrieving access log {}", url);

            try {
                org.apache.commons.vfs2.FileObject remoteFileObject = fsManager.resolveFile(url, fsOptions);
                if (!remoteFileObject.exists()) {
                    LOGGER.error("File {} does not exist", remoteFileObject);
                    if (censusConfig.isAcid()) {
                        throw new RuntimeException("Unable to download file!");
                    }
                    continue;
                }

                // create temporary file to copy the content to
                File tempFile = File.createTempFile("census" + fileIndex + "log_", ".txt");
                FileObject localFileObject = fsManager.resolveFile(tempFile.getAbsolutePath());

                // copy content
                LOGGER.debug("Copy the content to temporary file {}", tempFile);
                FileUtil.copyContent(remoteFileObject, localFileObject);

                retrievedFiles.add(tempFile);

                fileIndex++;

            } catch (FileSystemException e) {
                LOGGER.error("Failed to retrieve log file {}: {}", url, e.getMessage());
                if (censusConfig.isAcid()) {
                    throw e;
                }
            } catch (IOException e) {
                LOGGER.error("Failed to retrieve log file {}: {}", url, e.getMessage());
                if (censusConfig.isAcid()) {
                    throw e;
                }
            }
        }

        return retrievedFiles;
    }

    private LogRecordFactory getLogRecordFactory(LogSubject logSubject) {
        return logSubject.getLogPattern();
    }

    private void sleep() {
        LOGGER.trace("Sleeping {} ms", getCheckInterval());
        synchronized (this) {
            try {
                wait(getCheckInterval());
            } catch (InterruptedException e) {
            }
        }
    }

    private Subject asModel(LogSubject logSubject) {
        Subject subject = new Subject();
        subject.setId(logSubject.getId());
        subject.setName(logSubject.getName());
        return subject;
    }
}
