package lb.census.record.recorders;

import lb.census.model.DayStats;
import lb.census.model.Subject;

public class RecorderContext {

    private Subject subject;
    private DayStats currentDayStats;
    private int imported;
    private int filtered;

    public RecorderContext(Subject subject) {
        this.subject = subject;
    }

    public Subject getSubject() {
        return subject;
    }

    public DayStats getCurrentDayStats() {
        return currentDayStats;
    }

    public void setCurrentDayStats(DayStats currentDayStats) {
        this.currentDayStats = currentDayStats;
    }

    public void addImported(int total) {
        this.imported += total;
    }

    public void addFiltered(int total) {
        this.filtered += total;
    }

    public int getImported() {
        return imported;
    }

    public int getFiltered() {
        return filtered;
    }
}
