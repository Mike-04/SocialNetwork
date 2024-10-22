package repo.file;

import domain.Friendship;
import domain.User;
import domain.validators.Validator;
import repo.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

public class FriendshipFileRepo extends AbstractFileRepository<UUID, Friendship> {

    Repository<UUID, User> userRepo;

    public FriendshipFileRepo(String fileName, Validator<Friendship> validator, Repository<UUID, User> userRepo) {
        super(fileName, validator);
        if (userRepo == null)
            throw new IllegalArgumentException("User repository cannot be null");
        this.userRepo = userRepo;
        super.loadFromFile();
    }

    @Override
    protected Friendship createEntity(String line) {
        String[] splited = line.split(";");
        //print all users

        if(userRepo != null){
            User u1 = userRepo.findOne(UUID.fromString(splited[0]));
            User u2 = userRepo.findOne(UUID.fromString(splited[1]));
            if(u1 == null || u2 == null)
                return null;
            System.out.println(splited[0]+"+"+splited[1]);

            Friendship f = new Friendship(u1, u2);
            f.setId(UUID.fromString(splited[2]));
            f.setFriendshipDate(LocalDateTime.parse(splited[3]));

            u1.addFriend(u2);
            u2.addFriend(u1);
            return f;
        }
        return null;
    }

    @Override
    protected String saveEntity(Friendship entity) {
        return entity.getUser1().getId() + ";" + entity.getUser2().getId() + ";" + entity.getId() + ";" + entity.getFriendshipDate();
    }
}
