package service;

import domain.Friendship;
import domain.User;
import repo.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Service {
    private final UserController userController;
    private final FriendshipController friendshipController;


    public Service(Repository<UUID, User> userRepo, Repository<UUID, Friendship> friendshipRepo) {
        this.userController = new UserController(userRepo, friendshipRepo);
        this.friendshipController = new FriendshipController(friendshipRepo, userRepo);
    }

    public User getUserByUsername(String username) {
       return userController.getUserByUsername(username);
    }

    public void addUser(String firstName, String lastName, String username) {
        userController.addUser(firstName, lastName, username);
    }

    public void deleteUser(String username) {
        userController.deleteUser(username);
    }

    public List<User> getAllUsers() {
        return userController.getAllUsers();
    }

    public void addFriendship(String username1, String username2) {
        friendshipController.addFriendship(username1, username2);
    }

    public void removeFriendship(String username1, String username2) {
        friendshipController.removeFriendship(username1, username2);
    }

    public List<Friendship> getAllFriendships() {
        return friendshipController.getAllFriendships();
    }

    private List<User> dfs(User user, Set<User> visited) {
        List<User> connectedComponent = new ArrayList<>();
        visited.add(user);
        connectedComponent.add(user);
        user.getFriends().forEach(friend -> {
            if (!visited.contains(friend)) {
                //print the friends of the friend
//                friend.getFriends().forEach(System.out::println);
                connectedComponent.addAll(dfs(friend, visited));
            }
        });
        return connectedComponent;
    }

    public List<User> getBiggestCommunity() {
        Set<User> visited = new HashSet<>();
        List<User> biggestCommunity = new ArrayList<>();

        userController.getAllUsers().forEach(user -> {
            if (!visited.contains(user)) {
                List<User> community = dfs(user, visited);
                if (community.size() > biggestCommunity.size()) {
                    biggestCommunity.clear();
                    biggestCommunity.addAll(community);
                }
            }
        });
        return biggestCommunity;
    }

    public int getNumberOfCommunities() {
        Set<User> visited = new HashSet<>();
        AtomicInteger count = new AtomicInteger();

        userController.getAllUsers().forEach(user -> {
            if (!visited.contains(user)) {
                count.getAndIncrement();
                dfs(user, visited);
            }
        });
        return count.get();
    }


    public List<User> getFriendRequests(String username) {
        User user = getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        List<User> friendRequests = new ArrayList<>();
        //get all friendships from the repo where the user is the second user
        friendshipController.getAllFriendships().forEach(friendship -> {
            if (friendship.getUser2().equals(user) && friendship.getStatus() == 0) {
                friendRequests.add(friendship.getUser1());
            }
        });
        return friendRequests;
    }

    public void acceptFriendRequest(String username1, String username2) {
        friendshipController.acceptFriendRequest(username1, username2);
    }

    public void denyFriendRequest(String username1, String username2) {
        friendshipController.denyFriendRequest(username1, username2);
    }

    public int getFriendshipStatus(String username1, String username2) {
        return friendshipController.getFriendshipStatus(username1, username2);
    }

    public List<User> getPotentialFriend(String username) {
        ArrayList<User> potentialFriends = new ArrayList<>();

        //get all users that are not friends with the user and are not the user or do not have a pending friendship
        getAllUsers().forEach(user -> {
            System.out.println(getFriendshipStatus(username, user.getUsername())  +" " + user.getUsername());
            if (!user.getUsername().equals(username) && !userController.getFriendsOfUser(username).contains(user) && getFriendshipStatus(username, user.getUsername()) == -1) {
                potentialFriends.add(user);
            }
        });
        for (User u : potentialFriends) {
            System.out.println(u.getUsername());
        }
        return potentialFriends;
    }

    public User getFriendshipSender(String user1, String user2) {
        return friendshipController.getFriendshipSender(user1, user2);
    }

    public LocalDateTime getFriendshipDate(String user1, String user2){
        return friendshipController.getFriendshipDate(user1, user2);
    }

    public ArrayList<User> getFriendsAndFriendRequestOfUser(String username) {
        ArrayList<User> friends = new ArrayList<>();
        User user = getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        userController.getFriendsOfUser(username).forEach(friends::add);
        getFriendRequests(username).forEach(friends::add);
        friends.forEach(f -> System.out.println(f.getUsername()));
        return friends;
    }

}
