package lb.census.record;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by philippeschottey on 05/03/2017.
 */
public class DefaultLogSet extends LogSet {

    private List<Stream<String>> logData;

    public void setLogData(List<Stream<String>> logData) {
        this.logData = logData;
    }

    @Override
    public List<Stream<String>> getLogData() {
        return logData;
    }
}
