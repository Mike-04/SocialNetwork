package repo.memo;

import domain.Entity;
import domain.validators.Validator;
import repo.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    private Validator<E> validator;
    protected Map<ID, E> entities;

    public InMemoryRepository() {}
    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<>();
    }

    @Override
    public Optional<E> findOne(ID id) {
        if (id == null)
            throw new IllegalArgumentException("how did a null id even get here? Hecker?");
        // Use Optional.ofNullable to handle null values in Optional
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity) {
        //validate the entity
        if (entity == null)
            throw new IllegalArgumentException("HOW DID A NULL ENTITY EVEN GET HERE? HACKER?");
        validator.validate(entity);
        if (entities.containsKey(entity.getId())) {
            return Optional.of(entities.get(entity.getId()));  // Entity exists
        }
        entities.put(entity.getId(), entity);  // Save entity
        return Optional.empty();  // Indicate successful save with no existing entity
    }


    @Override
    public Optional<E> delete(ID id) {
        if (id == null)
            throw new IllegalArgumentException("how did a null id even get here? Hecker?");

        // Remove the entity if it exists and return it wrapped in Optional
        return Optional.ofNullable(entities.remove(id));
    }

    @Override
    public Optional<E> update(E entity) {
        if (entity == null)
            throw new IllegalArgumentException("HOW DID A NULL ENTITY EVEN GET HERE? HACKER?");
        validator.validate(entity);

        // If entity does not exist, return it wrapped in Optional
        if (!entities.containsKey(entity.getId())) {
            return Optional.of(entity);
        } else {
            entities.put(entity.getId(), entity);
            return Optional.empty(); // Return empty Optional if update is successful
        }
    }
}
