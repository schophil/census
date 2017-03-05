package lb.census.utils;

/**
 * Created by philippeschottey on 05/03/2017.
 */
public abstract class StreamMonitor<T> {

    public T inspect(T element) {
        doSomething(element);
        return element;
    }

    protected abstract void doSomething(T element);
}
