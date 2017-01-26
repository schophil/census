package lb.census.record.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author psc
 */
@XmlRootElement(name = "csv")
public class CsvLog extends LogRecordFactory {

    private static final Logger logger = LoggerFactory.getLogger(CsvLog.class);
    private String columnSeparator;
    private int userIdColumn;
    private int responseTimeColumn;
    private int resultCodeColumn;
    private int sourceIpColumn;
    private int timestampColumn;
    private String timestampFormat;
    private DateFormat timestampDateFormat;
    private int pathColumn;

    public int getPathColumn() {
        return pathColumn;
    }

    public void setPathColumn(int pathColumn) {
        this.pathColumn = pathColumn;
    }

    public int getTimestampColumn() {
        return timestampColumn;
    }

    public void setTimestampColumn(int timestampColumn) {
        this.timestampColumn = timestampColumn;
    }

    public String getTimestampFormat() {
        return timestampFormat;
    }

    public void setTimestampFormat(String timestampFormat) {
        this.timestampFormat = timestampFormat;
        this.timestampDateFormat = new SimpleDateFormat(getTimestampFormat());
    }

    public int getSourceIpColumn() {
        return sourceIpColumn;
    }

    public void setSourceIpColumn(int sourceIpColumn) {
        this.sourceIpColumn = sourceIpColumn;
    }

    public String getColumnSeparator() {
        return columnSeparator;
    }

    public void setColumnSeparator(String columnSeparator) {
        this.columnSeparator = columnSeparator;
    }

    public int getUserIdColumn() {
        return userIdColumn;
    }

    public void setUserIdColumn(int userIdColumn) {
        this.userIdColumn = userIdColumn;
    }

    public int getResponseTimeColumn() {
        return responseTimeColumn;
    }

    public void setResponseTimeColumn(int responseTimeColumn) {
        this.responseTimeColumn = responseTimeColumn;
    }

    public int getResultCodeColumn() {
        return resultCodeColumn;
    }

    public void setResultCodeColumn(int resultCodeColumn) {
        this.resultCodeColumn = resultCodeColumn;
    }

    public CsvLog() {
    }

    @Override
    public LogRecord create(String lineFromLog) {
        try {
            String[] columns = lineFromLog.split(getColumnSeparator());

            LogRecord logRecord = new LogRecord();
            logRecord.setOriginalLine(lineFromLog);
            logRecord.setUserId(columns[getUserIdColumn()]);
            logRecord.setResultCode(columns[getResultCodeColumn()]);
            logRecord.setResponseTime(Double.parseDouble(columns[getResponseTimeColumn()]));
            logRecord.setSourceIp(columns[getSourceIpColumn()]);
            logRecord.setResource(columns[getPathColumn()]);

            String timestampAsString = columns[getTimestampColumn()];
            if (timestampAsString != null) {
                logRecord.setTimestamp(timestampDateFormat.parse(timestampAsString));
            }

            return logRecord;
        } catch (Throwable t) {
            logger.error("Unable to parse line {}: {}", lineFromLog, t.getMessage());
            return null;
        }
    }
}
