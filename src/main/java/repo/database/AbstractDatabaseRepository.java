package repo.database;

//conects to a postgresql database
import java.sql.Connection;
import java.util.Optional;

import domain.Entity;
import domain.validators.Validator;
import repo.memo.InMemoryRepository;

public abstract class AbstractDatabaseRepository <ID, E extends Entity<ID>> implements repo.Repository<ID, E> {
    private final Validator<E> validator;
    Connection connection;

    public AbstractDatabaseRepository(Connection connection, Validator<E> validator) {
        this.connection = connection;
        this.validator = validator;
    }

    @Override
    public abstract Optional<E> findOne(ID id);

    @Override
    public abstract Iterable<E> findAll();

    @Override
    public abstract Optional <E> update(E entity);

    @Override
    public abstract Optional<E> save(E entity);

    @Override
    public abstract Optional<E> delete(ID id);




}
