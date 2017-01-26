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

    @Override
    public String toString() {
        return "LogRecord{" + "responseTime=" + responseTime + ", resultCode=" + resultCode + ", userId=" + userId + ", sourceIp=" + sourceIp + ", timestamp=" + timestamp + '}';
    }
}
