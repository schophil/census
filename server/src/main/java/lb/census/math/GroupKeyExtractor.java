package lb.census.math;

public interface GroupKeyExtractor<K, V> {

    K getKey(V value);
}
