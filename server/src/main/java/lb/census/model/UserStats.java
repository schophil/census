package lb.census.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * Statistics for a specific date and user.
 */
@Entity
@Table(name = "census_userstats")
public class UserStats implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GenericGenerator(name = "CUSTOM", strategy = "lb.census.utils.UUIDGenerator")
    @GeneratedValue(generator = "CUSTOM")
    @Column(name = "id")
    private String id;
    @Column(name = "userid")
    private String userId;
    @Column(name = "rtotal")
    private Integer totalRequests;
    @Column(name = "rerror")
    private Integer totalRequestsInError;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "daystat")
    private DayStats dayStats;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public Integer getTotalRequestsInError() {
        return totalRequestsInError;
    }

    public void setTotalRequestsInError(Integer totalRequestsInError) {
        this.totalRequestsInError = totalRequestsInError;
    }

    public void setTotalRequests(Integer totalRequests) {
        this.totalRequests = totalRequests;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DayStats getDayStats() {
        return dayStats;
    }

    public void setDayStats(DayStats dayStats) {
        this.dayStats = dayStats;
    }
}
