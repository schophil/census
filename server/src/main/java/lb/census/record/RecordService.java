package lb.census.record;

import lb.census.model.Subject;

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
    void recordFiles(LogSet logSet, Subject subject);
}
