package lb.census.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "census_sourceip")
public class SourceIp implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "CUSTOM", strategy = "lb.census.utils.UUIDGenerator")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "CUSTOM")
    private String id;
    @Column(name = "ip")
    private String ip;
    @Column(name = "userid")
    private String userId;
    @Column(name = "lused")
    @Temporal(TemporalType.DATE)
    private Date lastUsed;
    @Column(name = "subject")
    private String subject;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }

    public void touch() {
        setLastUsed(Calendar.getInstance().getTime());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
