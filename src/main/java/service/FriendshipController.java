package service;

import domain.Friendship;
import domain.User;
import repo.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FriendshipController {
    private final Repository<UUID, Friendship> friendshipRepo;
    private final Repository<UUID, User> userRepo;


    public FriendshipController(Repository<UUID, Friendship> friendshipRepo, Repository<UUID, User> userRepo) {
        this.friendshipRepo = friendshipRepo;
        this.userRepo = userRepo;
    }

    private User getUserByUsername(String username) {
        final User[] foundUser = {null}; // Use an array to hold the found user reference
        userRepo.findAll().forEach(user -> {
            if (user.getUsername().equals(username)) {
                foundUser[0] = user; // Set the found user
            }
        });
        return foundUser[0]; // Return the found user, or null if not found
    }

    public void addFriendship(String username1, String username2) {
        User u1 = Optional.ofNullable(getUserByUsername(username1))
                .orElseThrow(() -> new RuntimeException("User " + username1 + " does not exist"));
        User u2 = Optional.ofNullable(getUserByUsername(username2))
                .orElseThrow(() -> new RuntimeException("User " + username2 + " does not exist"));

        Friendship friendship = new Friendship(u1, u2);
        friendshipRepo.save(friendship).ifPresent(f -> {
            throw new RuntimeException("Failed to add friendship");
        });

        u1.addFriend(u2);
        u2.addFriend(u1);
    }

    public void removeFriendship(String username1, String username2) {
        User u1 = Optional.ofNullable(getUserByUsername(username1))
                .orElseThrow(() -> new RuntimeException("User " + username1 + " does not exist"));
        User u2 = Optional.ofNullable(getUserByUsername(username2))
                .orElseThrow(() -> new RuntimeException("User " + username2 + " does not exist"));

        //to change the way we iterate
        Iterable<Friendship> friendships = friendshipRepo.findAll();
        for (Friendship f : friendships) {
            if (f.getUser1().equals(u1) && f.getUser2().equals(u2) || f.getUser1().equals(u2) && f.getUser2().equals(u1)) {
                friendshipRepo.delete(f.getId());
                u1.removeFriend(u2);
                u2.removeFriend(u1);
                return;
            }
        }
    }

    public List<Friendship> getAllFriendships() {
        List<Friendship> friendships = new ArrayList<>();
        friendshipRepo.findAll().forEach(friendships::add);
        return friendships;
    }

    public void acceptFriendRequest(String username1, String username2) {
        //accept the friendship between the two users
        User u1 = Optional.ofNullable(getUserByUsername(username1))
                .orElseThrow(() -> new RuntimeException("User " + username1 + " does not exist"));
        User u2 = Optional.ofNullable(getUserByUsername(username2))
                .orElseThrow(() -> new RuntimeException("User " + username2 + " does not exist"));

        //update the friendship status
        Iterable<Friendship> friendships = friendshipRepo.findAll();
        for (Friendship f : friendships) {
            if (f.getUser1().equals(u1) && f.getUser2().equals(u2) && f.getStatus() == 0 || f.getUser1().equals(u2) && f.getUser2().equals(u1) && f.getStatus() == 0) {
                //update the friendship status by making a new friendship with the same id
                Friendship friendship = new Friendship(u1, u2, LocalDateTime.now(), 1);
                friendship.setId(f.getId());
                System.out.println(friendship.getId());
                System.out.println(f.getId());
                System.out.println(friendship);
                friendshipRepo.update(friendship);
                u1.addFriend(u2);
                u2.addFriend(u1);
                return;
            }
        }

    }

    public void denyFriendRequest(String username1, String username2) {
        removeFriendship(username1, username2);
    }

    public int getFriendshipStatus(String username1, String username2) {
        User u1 = Optional.ofNullable(getUserByUsername(username1))
                .orElseThrow(() -> new RuntimeException("User " + username1 + " does not exist"));
        User u2 = Optional.ofNullable(getUserByUsername(username2))
                .orElseThrow(() -> new RuntimeException("User " + username2 + " does not exist"));

        Iterable<Friendship> friendships = friendshipRepo.findAll();
        for (Friendship f : friendships) {
            if (f.getUser1().equals(u1) && f.getUser2().equals(u2) || f.getUser1().equals(u2) && f.getUser2().equals(u1)) {
                return f.getStatus();
            }
        }
        return -1;
    }

    public User getFriendshipSender(String user1, String user2){
        User u1 = Optional.ofNullable(getUserByUsername(user1))
                .orElseThrow(() -> new RuntimeException("User " + user1 + " does not exist"));
        User u2 = Optional.ofNullable(getUserByUsername(user2))
                .orElseThrow(() -> new RuntimeException("User " + user2 + " does not exist"));

        Iterable<Friendship> friendships = friendshipRepo.findAll();
        for (Friendship f : friendships) {
            if (f.getUser1().equals(u1) && f.getUser2().equals(u2) && f.getStatus() == 0) {
                return f.getUser1();
            }
        }
        return null;
    }

    public LocalDateTime getFriendshipDate(String user1, String user2){
        User u1 = Optional.ofNullable(getUserByUsername(user1))
                .orElseThrow(() -> new RuntimeException("User " + user1 + " does not exist"));
        User u2 = Optional.ofNullable(getUserByUsername(user2))
                .orElseThrow(() -> new RuntimeException("User " + user2 + " does not exist"));

        Iterable<Friendship> friendships = friendshipRepo.findAll();
        for (Friendship f : friendships) {
            if (f.getUser1().equals(u1) && f.getUser2().equals(u2) || f.getUser1().equals(u2) && f.getUser2().equals(u1)) {
                return f.getFriendshipDate();
            }
        }
        return null;
    }

}
