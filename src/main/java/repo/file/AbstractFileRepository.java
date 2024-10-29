package repo.file;

import domain.Entity;
import domain.validators.Validator;
import repo.memo.InMemoryRepository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

/**
 * @param <ID> - the type of the id of the entity
 * @param <E> - the type of the entity
 *           Abstract class for a file repository
 *           A file repository is defined by a filename
 */
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    protected String filename;

    /**
     * @param line - the line from the file
     * @return the entity created from the line
     */
    protected abstract E createEntity(String line);

    /**
     * @param entity - the entity to be saved
     * @return the string representation of the entity
     */
    protected abstract String saveEntity(E entity);

    /**
     * @param filename - the name of the file
     * @param validator - the validator of the entity
     */
    public AbstractFileRepository(String filename, Validator<E> validator) {
        super(validator);
        this.filename = filename;
    }

    /**
     * Loads entities from file.
     * If an entity is loaded successfully, it is saved in memory.
     */
    protected synchronized void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                E entity = createEntity(line);
                if (entity != null) {
                    super.save(entity);  // Load entity into memory
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves entities to file.
     * If an entity is saved successfully, it is saved in memory.
     */
    protected synchronized void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, false))) {
            for (E entity : entities.values()) {
                String ent = saveEntity(entity);
                bw.write(ent);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<E> save(E entity) {
        Optional<E> existingEntity = super.save(entity);
        if (existingEntity.isEmpty()) {
            saveToFile();
        }
        return existingEntity;
    }

    @Override
    public Optional<E> delete(ID id) {
        Optional<E> deletedEntity = super.delete(id);
        if (deletedEntity.isPresent()) {
            saveToFile();
        }
        return deletedEntity;
    }

    @Override
    public Optional<E> update(E entity) {
        Optional<E> updatedEntity = super.update(entity);
        if (updatedEntity.isEmpty()) {
            saveToFile();
        }
        return updatedEntity;
    }
}
