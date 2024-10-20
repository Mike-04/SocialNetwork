package repo.memo;

import domain.Entity;
import domain.validators.Validator;
import repo.Repository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {
    private Validator<E> validator;
    protected Map<ID,E> entities;

    public InMemoryRepository() {}
    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    @Override
    public E findOne(ID id) {
        if(id == null)
            throw new IllegalArgumentException("how did a null id even get here? Hecker?");
        return entities.get(id);
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public E save(E entity) {
        if (entity == null)
            throw new IllegalArgumentException("HOW DID A NULL ENTITY EVEN GET HERE? HACKER?");
        validator.validate(entity);
        if(entities.get(entity.getId()) != null)
            return entity;
        else{
            entities.put(entity.getId(),entity);
            return null;
        }
    }

    @Override
    public E delete(ID id) {
        if(id == null)
            throw new IllegalArgumentException("how did a null id even get here? Hecker?");

        E e = entities.get(id);

        if(e == null)
            return null;

        return entities.remove(e.getId());
    }

    @Override
    public E update(E entity) {
        if(entity == null)
            throw new IllegalArgumentException("HOW DID A NULL ENTITY EVEN GET HERE? HACKER?");
        validator.validate(entity);
        if(entities.get(entity.getId()) == null)
            return entity;
        else{
            entities.put(entity.getId(),entity);
            return null;
        }
    }
}
