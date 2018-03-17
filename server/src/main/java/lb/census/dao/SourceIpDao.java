package lb.census.dao;

import java.util.List;

import lb.census.model.SourceIp;

public interface SourceIpDao {

    SourceIp getSourceIP(String userId, String ip, String subject);

    void save(SourceIp sourceIP);

    /**
     * Get all the registered IP addresses of a user within the last X days.
     *
     * @param userId
     * @param lastDays
     * @return
     */
    List<SourceIp> getSourceIPsFor(String userId, int lastDays);

    /**
     * Returns all recorded source ips in the X last days.
     *
     * @param lastDays
     * @return
     */
    List<SourceIp> getAllSourceIPs(int lastDays);

    /**
     * Searches source ips base on a wildcard search in user id and ip.
     *
     * @param query
     * @param lastDays
     * @return
     */
    List<SourceIp> searchSourceIPs(String query, int lastDays);

    int deleteSourceIPsOf(int lastDays);
}
