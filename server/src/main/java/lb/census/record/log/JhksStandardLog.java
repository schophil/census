package lb.census.record.log;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by phili on 21/12/2015.
 */
@XmlRootElement(name = "jhks")
public class JhksStandardLog extends CsvLog {

    public JhksStandardLog() {
        setColumnSeparator(";");
        setSourceIpColumn(2);
        setResponseTimeColumn(10);
        setResultCodeColumn(9);
        setTimestampColumn(3);
        setUserIdColumn(4);
        setTimestampFormat("[dd/MMM/yyyy:HH:mm:ss Z]");
        setPathColumn(6);
        setMethodColumn(5);
    }
}
