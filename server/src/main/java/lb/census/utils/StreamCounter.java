package lb.census.utils;

/**
 * Created by philippeschottey on 04/03/2017.
 */
public class StreamCounter<T> extends StreamMonitor<T> {

    private int counter = 0;

    public void doSomething(T element) {
        counter++;
    }

    public int getCount() {
        return counter;
    }
}
