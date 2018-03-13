package lb.census.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Holds statistics of a specific date. This is the main entity for a specific
 * date, all other statistics reference this.
 */
@Entity
@Table(name = "census_daystats")
public class DayStats implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "CUSTOM", strategy = "lb.census.utils.UUIDGenerator")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "CUSTOM")
    @Column(name = "id")
    private String id;
    @Column(name = "sdate")
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
    private Date date;
    @Column(name = "subject")
    private String subject;
    @Column(name = "rtotal")
    private Integer totalRequests;
    @Column(name = "rerror")
    private Integer totalRequestsInError;
    @Column(name = "raverage")
    private Double averageResponseTime;
    @Column(name = "rmax")
    private Double maxResponseTime;
    @Column(name = "rmin")
    private Double minResponseTime;
    @Column(name = "utotal")
    private Integer totalUserIds;
    @OneToMany(mappedBy = "dayStats", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("totalRequests DESC")
    private Set<UserStats> userStats;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Integer getTotalUserIds() {
        return totalUserIds;
    }

    public void setTotalUserIds(Integer totalUserIds) {
        this.totalUserIds = totalUserIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Set<UserStats> getUserStats() {
        return userStats;
    }

    public void setUserStats(Set<UserStats> userStats) {
        this.userStats = userStats;
    }

    public Integer getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Integer totalRequests) {
        this.totalRequests = totalRequests;
    }

    public Integer getTotalRequestsInError() {
        return totalRequestsInError;
    }

    public void setTotalRequestsInError(Integer totalRequestsInError) {
        this.totalRequestsInError = totalRequestsInError;
    }

    public Double getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(Double averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public Double getMaxResponseTime() {
        return maxResponseTime;
    }

    public void setMaxResponseTime(Double maxResponseTime) {
        this.maxResponseTime = maxResponseTime;
    }

    public Double getMinResponseTime() {
        return minResponseTime;
    }

    public void setMinResponseTime(Double minResponseTime) {
        this.minResponseTime = minResponseTime;
    }

    @Transient
    public List<UserStats> getUserStatsList() {
        List<UserStats> userStatsList = new ArrayList<>(getUserStats().size());
        if (getUserStats() != null) {
            userStatsList.addAll(getUserStats());
        }
        return userStatsList;
    }

    @Override
    public String toString() {
        return "DayStats[" + getId() + ", " + getDate() + ", #"
                + (getUserStats() == null ? 0 : getUserStats().size()) + "]";
    }
}
