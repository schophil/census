package lb.census.model;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

/**
 * Common data for activity per hour. This class contains the total number of
 * requests handled on a specific date and hour.
 *
 * @author phili
 * @since 29/10/14
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ActivityPerHour {

    @Id
    @GenericGenerator(name = "CUSTOM", strategy = "lb.census.utils.UUIDGenerator")
    @GeneratedValue(generator = "CUSTOM")
    @Column(name = "id")
    private String id;
    @Column(name = "dhour")
    private Integer hour;
    @Column(name = "hits")
    private Integer hits;
    @Column(name = "raverage")
    private Double averageResponseTime;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "daystat")
    private DayStats dayStats;

    public Double getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(Double averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public DayStats getDayStats() {
        return dayStats;
    }

    public void setDayStats(DayStats dayStats) {
        this.dayStats = dayStats;
    }

}
