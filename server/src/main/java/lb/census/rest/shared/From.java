package lb.census.rest.shared;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

/**
 * Created by philippeschottey on 03/03/2017.
 */
public class From implements Period {

    private Date date;
    private int days;

    public From(Date date, int days) {
        this.date = date;
        this.days = days;
    }

    @Override
    public Date getFrom() {
        return DateUtils.addDays(date, 0 - days);
    }

    @Override
    public Date getTo() {
        return date;
    }

    @Override
    public String toString() {
        return "From{" +
                "date=" + date +
                ", days=" + days +
                '}';
    }
}
