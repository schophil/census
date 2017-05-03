package lb.census.record.scheduler;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lb.census.config.LogSubject;

/**
 * Represents a scheduled retrieval;
 *
 * @author Philippe
 */
public class ScheduledRetrieval implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private ScheduledRetrievalState state;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "CET")
    private final Date scheduledOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "CET")
    private Date startedOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
    private final Date target;
    private LogSubject logSubject;

    public ScheduledRetrieval(Date target, LogSubject logSubject) {
        scheduledOn = new Date();
        state = ScheduledRetrievalState.SCHEDULED;
        this.target = target;
        this.logSubject = logSubject;
    }

    public LogSubject getLogSubject() {
        return logSubject;
    }

    public void setLogSubject(LogSubject logSubject) {
        this.logSubject = logSubject;
    }

    public Date getTarget() {
        return target;
    }

    public ScheduledRetrievalState getState() {
        return state;
    }

    public Date getScheduledOn() {
        return scheduledOn;
    }

    public Date getStartedOn() {
        return startedOn;
    }

    public boolean isStopped() {
        return state == ScheduledRetrievalState.SUCCESS
                || state == ScheduledRetrievalState.FAILED;
    }

    public boolean isOlderThan(Date date) {
        return scheduledOn.compareTo(date) < 0;
    }

    protected void start() {
        state = ScheduledRetrievalState.STARTED;
        startedOn = new Date();
    }

    protected void stop(boolean success) {
        if (success) {
            state = ScheduledRetrievalState.SUCCESS;
        } else {
            state = ScheduledRetrievalState.FAILED;
        }
    }

    @Override
    public String toString() {
        return "ScheduledRetrieval{"
                + "state=" + state
                + ", scheduledOn=" + scheduledOn
                + ", startedOn=" + startedOn
                + ", target=" + target
                + '}';
    }
}
