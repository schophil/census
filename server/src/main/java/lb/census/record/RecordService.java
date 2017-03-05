package lb.census.record;

import lb.census.model.Subject;
import lb.census.record.recorders.RecorderContext;

/**
 * @author phili
 */
public interface RecordService {

    /**
     * Records a set of log files for a given subject.
     *
     * @param logSet
     * @param subject
     */
    RecorderContext record(LogSet logSet, Subject subject);
}
