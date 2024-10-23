package repo;

import domain.Entity;

/**
 * @param <ID> - the type of the id of the entity
 * @param <E> - the type of the entity
 *           Interface for a repository
 */
public interface Repository <ID,E extends Entity<ID>> {
    /**
     * @param id - the id of the entity to be returned
     * @return the entity with the given id
     *         Returns null if the entity does not exist
     *         Returns an Entity object
     */
    E findOne(ID id);

    /**
     * @return all entities
     *        Returns an Iterable of Entity objects
     */
    Iterable<E> findAll();

    /**
     * @param entity - the entity to be saved
     * @return null if the entity was saved successfully
     *        Returns the entity if the entity was already saved
     */
    E save(E entity);

    /**
     * @param id - the id of the entity to be deleted
     * @return the deleted entity
     *        Returns null if the entity does not exist
     */
    E delete(ID id);

    /**
     * @param entity - the entity to be updated
     * @return null if the entity was updated successfully
     *       Returns the entity if the entity does not exist
     */
    E update(E entity);
}
