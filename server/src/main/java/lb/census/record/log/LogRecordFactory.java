package lb.census.record.log;

/**
 * @author psc
 */
public abstract class LogRecordFactory {

    public abstract LogRecord create(String lineFromLog);
}
