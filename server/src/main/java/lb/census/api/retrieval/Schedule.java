package lb.census.api.retrieval;

import java.util.Calendar;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * Created by philippe on 23/04/16.
 */
public class Schedule {

    public String subject;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    public Calendar date;
}
