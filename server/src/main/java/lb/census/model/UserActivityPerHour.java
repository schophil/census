package lb.census.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ActivityPerHour for a user, for a specific hour on a specific date.
 *
 * @author phili
 * @since 26/10/14
 */
@Entity
@Table(name = "census_useractivity")
public class UserActivityPerHour extends ActivityPerHour implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "userid")
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
