import java.util.UUID;

import domain.*;
import repo.file.*;
import repo.*;
import domain.validators.*;
import service.*;


public class Main {
    public static void main(String[] args) {
        // do some testing here
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        Repository<UUID, User> userRepo = new UserFileRepo("users.txt", userValidator);
        Repository<UUID, Friendship> friendshipRepo = new FriendshipFileRepo("friendships.txt", friendshipValidator, userRepo);
        // print all friendships
        Service service = new Service(userRepo, friendshipRepo);
        //print all the friends of all users
        userRepo.findAll().forEach(user -> {
            System.out.println("User:");
            System.out.println(user);
            System.out.printf("Friends: %n");
            user.getFriends().forEach(System.out::println);
        });
        System.out.println(service.getNumberOfCommunities());

        friendshipRepo.findAll().forEach(System.out::println);

        service.getBiggestComunity().forEach(System.out::println);

    }
}
