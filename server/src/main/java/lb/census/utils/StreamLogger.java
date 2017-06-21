package lb.census.utils;

import org.slf4j.Logger;

/**
 * Created by philippeschottey on 05/03/2017.
 */
public class StreamLogger<T> extends StreamMonitor<T> {

    private Logger logger;
    private String name;

    public StreamLogger(Logger logger, String name) {
        this.logger = logger;
        this.name = name;
    }

    protected void doSomething(T element) {
        if (logger.isTraceEnabled()) {
            logger.trace("{}: {}", name, element);
        }
    }
}
