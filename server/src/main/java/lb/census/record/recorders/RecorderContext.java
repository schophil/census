package lb.census.record.recorders;

import lb.census.model.DayStats;
import lb.census.model.Subject;

public class RecorderContext {

    private Subject subject;
    private DayStats currentDayStats;

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
}
