package service;

import domain.Friendship;
import domain.User;
import repo.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserController {
    private final Repository<UUID, User> userRepo;
    private final Repository<UUID, Friendship> friendshipRepo;

    public List<User> getFriendsOfUser(String username) {
        User user = getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        return new ArrayList<>(user.getFriends());
    }

    UserController(Repository<UUID, User> userRepo, Repository<UUID, Friendship> friendshipRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
    }

    public User getUserByUsername(String username) {
        final User[] foundUser = {null}; // Use an array to hold the found user reference
        userRepo.findAll().forEach(user -> {
            if (user.getUsername().equals(username)) {
                foundUser[0] = user; // Set the found user
            }
        });
        return foundUser[0]; // Return the found user, or null if not found
    }

    public void addUser(String firstName, String lastName, String username) {
        if (getUserByUsername(username) != null) {
            throw new RuntimeException("User already exists");
        }
        User user = new User(firstName, lastName, username);
        // Save the user and throw an exception if it fails
        //get the return of save
        userRepo.save(user).ifPresent(u -> {
            throw new RuntimeException("Failed to add user");
        });
    }

    public void deleteUser(String username) {
        User user = Optional.ofNullable(getUserByUsername(username)).orElseThrow(() -> new RuntimeException("User does not exist1"));

        userRepo.delete(user.getId());

        List<Friendship> friendshipsToRemove = new ArrayList<>();
        friendshipRepo.findAll().forEach(friendship -> {
            if (friendship.getUser1().equals(user) || friendship.getUser2().equals(user)) {
                friendshipsToRemove.add(friendship);
            }
        });

        friendshipsToRemove.forEach(friendship -> friendshipRepo.delete(friendship.getId()));
        userRepo.findAll().forEach(u -> u.removeFriend(user));
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        userRepo.findAll().forEach(users::add);
        return users;
    }

}
