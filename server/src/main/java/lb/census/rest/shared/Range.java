package lb.census.rest.shared;

import java.util.Date;

/**
 * Created by philippeschottey on 03/03/2017.
 */
public class Range implements Period {

    private Date from;
    private Date to;

    public Range(Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Date getFrom() {
        return from;
    }

    @Override
    public Date getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "Range{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
