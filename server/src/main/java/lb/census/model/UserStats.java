package lb.census.model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

/**
 * Statistics for a specific date and user.
 */
@Entity
@Table(name = "census_userstats")
public class UserStats extends DefaultMetrics implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GenericGenerator(name = "CUSTOM", strategy = "lb.census.utils.UUIDGenerator")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "CUSTOM")
    private String id;
    @Column(name = "userid")
    private String userId;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "daystat")
    private DayStats dayStats;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
