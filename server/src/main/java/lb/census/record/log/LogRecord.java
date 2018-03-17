package lb.census.record.log;

import java.util.Date;

public class LogRecord {

    private String originalLine;
    private double responseTime;
    private String resultCode;
    private String userId;
    private String sourceIp;
    private Date timestamp;
    private String resource;
    private String method;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getOriginalLine() {
        return originalLine;
    }

    public void setOriginalLine(String originalLine) {
        this.originalLine = originalLine;
    }

    public boolean hasResultCode(String code) {
        return code.equals(resultCode);
    }

    public double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public LogRecord() {
    }

    public LogRecord(String userId) {
        this.userId = userId;
    }

    public LogRecord(String originalLine, double responseTime, String resultCode, String userId, String sourceIp, Date timestamp, String resource, String method) {
        this.originalLine = originalLine;
        this.responseTime = responseTime;
        this.resultCode = resultCode;
        this.userId = userId;
        this.sourceIp = sourceIp;
        this.timestamp = timestamp;
        this.resource = resource;
        this.method = method;
    }

    @Override
    public String toString() {
        return "LogRecord{" + "responseTime=" + responseTime + ", resultCode=" + resultCode + ", userId=" + userId + ", sourceIp=" + sourceIp + ", timestamp=" + timestamp + '}';
    }
}
