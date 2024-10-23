package domain.validators;

/**
 * @param <T> - the type of the entity to be validated
 *           Interface for a validator
 */
public interface Validator <T> {
    void validate(T entity) throws ValidationException;
}
