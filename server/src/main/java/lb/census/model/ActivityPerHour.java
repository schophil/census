package lb.census.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Common data for activity per hour. This class contains the total number of
 * requests handled on a specific date and hour.
 *
 * @author phili
 * @since 29/10/14
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ActivityPerHour extends DefaultMetrics {

    @Id
    @GenericGenerator(name = "CUSTOM", strategy = "lb.census.utils.UUIDGenerator")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "CUSTOM")
    private String id;
    @Column(name = "dhour")
    private Integer hour;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "daystat")
    private DayStats dayStats;

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

    public DayStats getDayStats() {
        return dayStats;
    }

    public void setDayStats(DayStats dayStats) {
        this.dayStats = dayStats;
    }

}
