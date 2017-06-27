/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lb.census.record.filters;

import lb.census.record.log.LogRecord;

/**
 * @author psc
 */
public abstract class Filter {

    /**
     * Checks if an un-materialized log line passes the filter.
     *
     * @param line
     * @return true if the line passes the filter and therefore can be recorded
     */
    public abstract boolean passes(String line);

    /**
     * Checks if a materialized log line passes the filter. This kind of filter
     * can also modify the materialized log line.
     *
     * @param logRecord
     * @return true if the record passes the filter and therefore can be
     * recorded
     */
    public abstract boolean passes(LogRecord logRecord);

    public Filter invert() {
        return new Invert(this);
    }
}
