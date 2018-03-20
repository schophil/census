package lb.census.rest.subjects.stats;

import lb.census.model.ActivityPerHour;
import lb.census.rest.shared.Metrics;

/**
 * Created by philippe on 30/12/15.
 */
public class OneHour extends Metrics {

    public Integer hour;

    /**
     * Creates a new {@link OneHour} instance based on an {@link ActivityPerHour} instance.
     *
     * @param activityPerHour
     * @return A new {@link OneHour} instance.
     */
    public static OneHour of(ActivityPerHour activityPerHour) {
        if (activityPerHour == null) {
            throw new IllegalArgumentException("Activity per hour is null!");
        }
        OneHour oneHour = new OneHour();
        oneHour.map(activityPerHour);
        oneHour.hour = activityPerHour.getHour();
        return oneHour;
    }
}
