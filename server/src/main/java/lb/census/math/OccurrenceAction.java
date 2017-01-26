package lb.census.math;

/**
 * Created by philippe on 26/04/16.
 */
public interface OccurrenceAction<G, V> {

    void apply(G groupId, V value, Integer occurrences);
}
