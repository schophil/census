package lb.census.dao;

import java.util.List;

import lb.census.model.SourceIp;

public interface SourceIpDao {

    SourceIp getSourceIP(String userId, String ip, String subject);

    void save(SourceIp sourceIP);

    List<SourceIp> getSourceIPsFor(String userId, String subject);

    List<SourceIp> getSourceIPsFor(String userId, int lastDays, String subject);

    List<SourceIp> getSourceIPsFor(int lastDays, String subject);

    List<SourceIp> searchSourceIPs(String query, int lastDays);

    int deleteSourceIPsOf(int lastDays);
}
