package domain;


import java.io.Serializable;

/**
 * @param <ID> the type of the id of the entity
 *            Abstract class for an entity
 *            An entity is defined by an id
 */
public class Entity<ID> implements Serializable {
    private static final long serialVersionUID = 1L;
    private ID id;
    public ID getId() {
        return id;
    }
    public void setId(ID id) {
        this.id = id;
    }
}

