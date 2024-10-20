package repo.file;

import domain.User;
import domain.validators.Validator;

import java.util.UUID;

public class UserFileRepo extends AbstractFileRepository<UUID, User> {
    public UserFileRepo(String fileName, Validator<User> validator) {
        super(fileName, validator);
    }
    @Override
    protected User createEntity(String line) {
        String[] splited = line.split(";");
        User u = new User(splited[1], splited[2], splited[3]);
        u.setId(UUID.fromString(splited[0]));
        return u;
    }

    @Override
    protected String saveEntity(User entity) {
        return entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName() + ";" + entity.getUsername();
    }
}
