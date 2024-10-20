package domain.validators;

import domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        validateName(entity.getFirstName());
        validateName(entity.getLastName());
        validateName(entity.getUsername());
    }
    public void validateName(String name) throws ValidationException {
        if (name.equals("")) {
            throw new ValidationException("Name cannot be empty");
        }
        if (name.length() >= 100) {
            throw new ValidationException("Name must have less than 100 characters");
        }
    }


}
