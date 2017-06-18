package lb.census.model;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

/**
 * Created by philippe on 13/05/15.
 */
@Entity
@Table(name = "census_resource")
public class Resource {

    @Id
    @GenericGenerator(name = "CUSTOM", strategy = "lb.census.utils.UUIDGenerator")
    @GeneratedValue(generator = "CUSTOM")
    @Column(name = "id")
    private String id;
    @Column(name = "hits")
    private Integer hits;
    @Column(name = "name")
    private String name;
    @Column(name = "value_nbr")
    private Integer numberValue;
    @Column(name = "value_txt")
    private String textValue;
    @Column(name = "value_txtlng")
    private String longTextValue;
    @Column(name = "userid")
    private String userId;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "daystat")
    private DayStats dayStats;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumberValue() {
        return numberValue;
    }

    public void setNumberValue(Integer numberValue) {
        this.numberValue = numberValue;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public String getLongTextValue() {
        return longTextValue;
    }

    public void setLongTextValue(String longTextValue) {
        this.longTextValue = longTextValue;
    }

    public DayStats getDayStats() {
        return dayStats;
    }

    public void setDayStats(DayStats dayStats) {
        this.dayStats = dayStats;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
