package lb.census.record.metrics;

import lb.census.record.log.LogRecord;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class SubKeyMetricsCollectorTest {

    @Test
    public void testSubKeys() {
        SubKeyMetricsCollector<CounterMetrics> collector = new SubKeyMetricsCollector<>(
                lr -> lr.getUserId(),
                () -> new CounterMetrics()
        );

        LogRecord[] records = {
                makeLogRecord("a"),
                makeLogRecord("b"),
                makeLogRecord("c"),
                makeLogRecord("a"),
                makeLogRecord("b"),
                makeLogRecord("x")
        };

        for (LogRecord logRecord : records) {
            collector.add(logRecord);
        }

        HashMap<String, CounterMetrics> map = collector.getMetricsCollectors();
        Assert.assertTrue(map.containsKey("a"));
        Assert.assertTrue(map.containsKey("b"));
        Assert.assertTrue(map.containsKey("c"));
        Assert.assertTrue(map.containsKey("x"));

        Assert.assertEquals(map.get("a").getCounter(), 2);
        Assert.assertEquals(map.get("b").getCounter(), 2);
        Assert.assertEquals(map.get("c").getCounter(), 1);
        Assert.assertEquals(map.get("x").getCounter(), 1);
    }

    private LogRecord makeLogRecord(String userId) {
        LogRecord lr = new LogRecord();
        lr.setUserId(userId);
        return lr;
    }
}
