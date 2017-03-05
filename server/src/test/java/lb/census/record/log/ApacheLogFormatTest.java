package lb.census.record.log;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by philippeschottey on 05/03/2017.
 */
public class ApacheLogFormatTest {

    @Test
    public void read() {
        ApacheLogFormat apacheLogFormat = new ApacheLogFormat();
        LogRecord logRecord = apacheLogFormat.create("127.0.0.1 - darthvader [02/Apr/2014:00:10:40 +0200] \"GET /manager/text/undeploy?path=/alor HTTP/1.1\" 200 61 0.2");
        assertNotNull(logRecord);

    }
}
