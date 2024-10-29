package repo.file;

import domain.Friendship;
import domain.User;
import domain.validators.Validator;
import repo.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Friendship file repository
 * Manages the friendships in a file
 */
public class FriendshipFileRepo extends AbstractFileRepository<UUID, Friendship> {

    private final Repository<UUID, User> userRepo;

    public FriendshipFileRepo(String fileName, Validator<Friendship> validator, Repository<UUID, User> userRepo) {
        super(fileName, validator);
        if (userRepo == null) {
            throw new IllegalArgumentException("User repository cannot be null");
        }
        this.userRepo = userRepo;
        super.loadFromFile();
    }

    @Override
    protected Friendship createEntity(String line) {
        String[] splited = line.split(";");
        if (userRepo != null) {
            // Use Optional and handle absence of users gracefully
            Optional<User> optionalUser1 = userRepo.findOne(UUID.fromString(splited[0]));
            Optional<User> optionalUser2 = userRepo.findOne(UUID.fromString(splited[1]));

            if (optionalUser1.isEmpty() || optionalUser2.isEmpty()) {
                return null;
            }

            User u1 = optionalUser1.get();
            User u2 = optionalUser2.get();

            Friendship friendship = new Friendship(u1, u2);
            friendship.setId(UUID.fromString(splited[2]));
            friendship.setFriendshipDate(LocalDateTime.parse(splited[3]));

            // Update both users with each other's friendship
            u1.addFriend(u2);
            u2.addFriend(u1);
            return friendship;
        }
        return null;
    }

    @Override
    protected String saveEntity(Friendship entity) {
        return entity.getUser1().getId() + ";" + entity.getUser2().getId() + ";" + entity.getId() + ";" + entity.getFriendshipDate();
    }
}
