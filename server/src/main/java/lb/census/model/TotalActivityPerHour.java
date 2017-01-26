package lb.census.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The total activity for a specific hour on a specific date.
 *
 * @author psc
 */
@Entity
@Table(name = "census_dayactivity")
public class TotalActivityPerHour extends ActivityPerHour implements Serializable {

    private static final long serialVersionUID = 1L;

    // inherits all fields
}
