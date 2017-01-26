package lb.census.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author phili
 * @since 23-Nov-2015
 */
@Entity
@Table(name = "census_subjects")
public class Subject implements Serializable {

	private static final long serialVersionUID = -5684973466898290223L;
	@Id
    private String id;
    @Column
    private String name;

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
}
