package lb.census.math;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OccurrenceCounter<G, V> {

    private final G defaultGroup;
    private final Map<G, Map<V, Integer>> groups = new HashMap<>();

    public OccurrenceCounter(G defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    public void register(V value) {
        this.register(defaultGroup, value);
    }

    public void register(G groupId, V value) {
        Map<V, Integer> group = groups.get(groupId);
        if (group == null) {
            // create new group
            group = new HashMap<>();
            groups.put(groupId, group);
        }
        registerInGroup(group, value);
    }

    public Integer getOccurrences(V value) {
        return this.getOccurrences(defaultGroup, value);
    }

    public Integer getOccurrences(G groupId, V value) {
        if (value == null || groupId == null) {
            return 0;
        }

        Map<V, Integer> group = groups.get(groupId);
        if (group == null) {
            return 0;
        }

        Integer counter = group.get(value);
        return counter == null ? 0 : counter;
    }

    public Set<G> getGroups() {
        return groups.keySet();
    }

    public void forEach(OccurrenceAction<G, V> action) {
        groups.forEach((groupId, occurrences) -> {
            occurrences.forEach((object, hits) -> {
                action.apply(groupId, object, hits);
            });
        });
    }

    public void clear() {
        groups.clear();
    }

    protected void registerInGroup(Map<V, Integer> group, V value) {
        Integer counter = group.get(value);
        if (counter == null) {
            group.put(value, 1);
        } else {
            group.put(value, counter + 1);
        }
    }
}
