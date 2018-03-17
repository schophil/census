package lb.census.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by philippe on 13/05/15.
 */
@Entity
@Table(name = "census_resource")
public class Resource extends DefaultMetrics implements Serializable {

    @Id
    @GenericGenerator(name = "CUSTOM", strategy = "lb.census.utils.UUIDGenerator")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "CUSTOM")
    private String id;
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
