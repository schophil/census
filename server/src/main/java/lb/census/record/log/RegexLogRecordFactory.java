package lb.census.record.log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author psc
 */
public class RegexLogRecordFactory extends LogRecordFactory {

    private static final Logger logger = LoggerFactory.getLogger(RegexLogRecordFactory.class);

    private Pattern pattern;
    private int userIdGroup;
    private int responseTimeGroup;
    private int resultCodeGroup;
    private int sourceIpGroup;
    private int timestampGroup;
    private String timestampFormat;
    private DateFormat timestampDateFormat;

    public void setRegex(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public int getUserIdGroup() {
        return userIdGroup;
    }

    public void setUserIdGroup(int userIdGroup) {
        this.userIdGroup = userIdGroup;
    }

    public int getResponseTimeGroup() {
        return responseTimeGroup;
    }

    public void setResponseTimeGroup(int responseTimeGroup) {
        this.responseTimeGroup = responseTimeGroup;
    }

    public int getResultCodeGroup() {
        return resultCodeGroup;
    }

    public void setResultCodeGroup(int resultCodeGroup) {
        this.resultCodeGroup = resultCodeGroup;
    }

    public int getSourceIpGroup() {
        return sourceIpGroup;
    }

    public void setSourceIpGroup(int sourceIpGroup) {
        this.sourceIpGroup = sourceIpGroup;
    }

    public int getTimestampGroup() {
        return timestampGroup;
    }

    public void setTimestampGroup(int timestampGroup) {
        this.timestampGroup = timestampGroup;
    }

    public String getTimestampFormat() {
        return timestampFormat;
    }

    public void setTimestampFormat(String timestampFormat) {
        this.timestampFormat = timestampFormat;
        this.timestampDateFormat = new SimpleDateFormat(timestampFormat);
    }

    @Override
    public LogRecord create(String lineFromLog) {
        Matcher matcher = pattern.matcher(lineFromLog);
        if (!matcher.matches()) {
            return null;
        }

        LogRecord logRecord = new LogRecord();
        logRecord.setOriginalLine(lineFromLog);
        logRecord.setSourceIp(matcher.group(getSourceIpGroup()));
        logRecord.setUserId(matcher.group(getUserIdGroup()));
        logRecord.setResultCode(matcher.group(getResultCodeGroup()));
        logRecord.setResponseTime(Double.parseDouble(matcher.group(getResponseTimeGroup())));
        String timestampAsString = matcher.group(getTimestampGroup());
        try {
            logRecord.setTimestamp(timestampDateFormat.parse(timestampAsString));
        } catch (ParseException ex) {
            logger.error("Unable to parse timestamp {} with format {}", timestampAsString, timestampDateFormat);

        }

        return logRecord;
    }

    public boolean matches(String lineFromLog) {
        return pattern.matcher(lineFromLog).matches();
    }
}
