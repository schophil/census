package lb.census.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grouper<K, V> {

    private final Map<K, List<V>> groups = new HashMap<>();

    public void add(K key, V value) {
        List<V> values = groups.get(key);
        if (values == null) {
            values = new ArrayList<>();
            groups.put(key, values);
        }
        values.add(value);
    }

    public void addAll(List<V> values, GroupKeyExtractor<K, V> groupKeyExtractor) {
        for (V value : values) {
            add(groupKeyExtractor.getKey(value), value);
        }
    }

    public Map<K, List<V>> getGroups() {
        return groups;
    }
}
