package lb.census.record.metrics;

import lb.census.record.log.LogRecord;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

public class SubHourMetricsCollectorTest {

    private Calendar calendar = Calendar.getInstance();

    @Test
    public void testSomeHours() {
        SubHourMetricsCollector<CounterMetrics> collector = new SubHourMetricsCollector<>(
                () -> new CounterMetrics()
        );

        LogRecord[] records = {
                makeLogRecord(2),
                makeLogRecord(4),
                makeLogRecord(5),
                makeLogRecord(2),
                makeLogRecord(3),
                makeLogRecord(5)
        };

        for (LogRecord logRecord : records) {
            collector.add(logRecord);
        }

        CounterMetrics[] metrics = collector.getMetricsCollectors(new CounterMetrics[24]);
        Assert.assertNotNull(metrics[2]);
        Assert.assertNotNull(metrics[4]);
        Assert.assertNotNull(metrics[5]);
        Assert.assertNotNull(metrics[3]);

        for (int i = 0; i < metrics.length; i++) {
            switch (i) {
                case 2:
                case 4:
                case 5:
                case 3:
                    Assert.assertNotNull(metrics[i]);
                    break;
                default:
                    Assert.assertNull(metrics[i]);
            }
        }

        Assert.assertEquals(metrics[2].getCounter(), 2);
        Assert.assertEquals(metrics[4].getCounter(), 1);
        Assert.assertEquals(metrics[5].getCounter(), 2);
        Assert.assertEquals(metrics[3].getCounter(), 1);
    }

    public LogRecord makeLogRecord(int hourOfTheDay) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfTheDay);
        LogRecord logRecord = new LogRecord();
        logRecord.setTimestamp(calendar.getTime());
        return logRecord;
    }
}
