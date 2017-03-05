package lb.census.record.log;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * Example record
 * <pre>
 * 127.0.0.1 - tomcat [02/Apr/2014:22:45:40 +0200] "GET /manager/text/undeploy?path=/alor HTTP/1.1" 200 61 0.2
 * </pre>
 *
 * @author psc
 */
@XmlRootElement(name = "apache")
public class ApacheLogFormat extends RegexLogRecordFactory {

    public ApacheLogFormat() {
        setRegex("(.*) (.*) (.*) \\[(.*)\\] \"(.*) (.*) (.*)\" (\\d*) (\\d*) ((\\d*)(\\.?)(\\d*))");
        setTimestampFormat("dd/MMM/yyyy:HH:mm:ss Z");
        setSourceIpGroup(1);
        setUserIdGroup(3);
        setResultCodeGroup(8);
        setTimestampGroup(4);
        setResponseTimeGroup(10);
    }
}
