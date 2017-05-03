package lb.census.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

/**
 * @author Philippe
 * @since 4-sep.-2014
 */
@Entity
@Table(name = "census_anomaly")
public class Anomaly implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GenericGenerator(name = "CUSTOM", strategy = "lb.census.utils.TemporalIdGenerator")
    @GeneratedValue(generator = "CUSTOM")
    @Column(name = "id")
    private String id;
    @Column(name = "userid")
    private String userId;
    @Temporal(TemporalType.DATE)
    @Column(name = "date")
    private Date date;
    @Column(name = "description")
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Anomaly[" + id + "]{" + date + ": " + getDescription() + "}";
    }
}
