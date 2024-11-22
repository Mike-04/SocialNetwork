package service;

import domain.Friendship;
import domain.User;
import repo.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Service implements Controller {

    private final Repository<UUID, User> userRepo;
    private final Repository<UUID, Friendship> friendshipRepo;

    /**
     * Creates a new Service object
     *
     * @param userRepo       the user repository
     * @param friendshipRepo the friendship repository
     */
    public Service(Repository<UUID, User> userRepo, Repository<UUID, Friendship> friendshipRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
    }

    /**
     * Returns a list of all friends of a user
     *
     * @param username the username of the user
     * @return a list of all friends of the user
     */
    public List<User> getFriendsOfUser(String username) {
        User user = getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        return new ArrayList<>(user.getFriends());
    }

    /**
     * Returns the user with the given username
     *
     * @param username the username of the user
     * @return the user with the given username, or null if not found
     */
    public User getUserByUsername(String username) {
        final User[] foundUser = {null}; // Use an array to hold the found user reference
        userRepo.findAll().forEach(user -> {
            if (user.getUsername().equals(username)) {
                foundUser[0] = user; // Set the found user
            }
        });
        return foundUser[0]; // Return the found user, or null if not found
    }

    /**
     * Adds a user to the social network
     *
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @param username  the username of the user
     */
    @Override
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

    /**
     * Deletes a user from the social network
     *
     * @param username the username of the user to be deleted
     */
    @Override
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

    /**
     * Adds a friendship between two users
     *
     * @param username1 the first user
     * @param username2 the second user
     */
    @Override
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

    /**
     * Removes a friendship between two users
     *
     * @param username1 the first user
     * @param username2 the second user
     */
    @Override
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

    /**
     * Returns a list of all users in the social network
     *
     * @return a list of all users
     */
    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        userRepo.findAll().forEach(users::add);
        return users;
    }

    /**
     * Returns a list of all friendships in the social network
     *
     * @return a list of all friendships
     */
    @Override
    public List<Friendship> getAllFriendships() {
        List<Friendship> friendships = new ArrayList<>();
        friendshipRepo.findAll().forEach(friendships::add);
        return friendships;
    }

    /**
     * Returns the biggest community in the social network
     *
     * @return the biggest community
     */
    @Override
    public List<User> getBiggestCommunity() {
        Set<User> visited = new HashSet<>();
        List<User> biggestCommunity = new ArrayList<>();

        userRepo.findAll().forEach(user -> {
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

    /**
     * Returns the number of communities in the social network
     *
     * @return the number of communities
     */
    @Override
    public int getNumberOfCommunities() {
        Set<User> visited = new HashSet<>();
        AtomicInteger count = new AtomicInteger();

        userRepo.findAll().forEach(user -> {
            if (!visited.contains(user)) {
                count.getAndIncrement();
                dfs(user, visited);
            }
        });
        return count.get();
    }

    /**
     * Performs a depth-first search on the graph of users
     *
     * @param user    the user to start the search from
     * @param visited a set of visited users
     * @return a list of connected users
     */
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

    //get user friends requests
    public List<User> getFriendRequests(String username) {
        User user = getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        List<User> friendRequests = new ArrayList<>();
        //get all friendships from the repo where the user is the second user
        friendshipRepo.findAll().forEach(friendship -> {
            if (friendship.getUser2().equals(user) && friendship.getStatus() == 0) {
                friendRequests.add(friendship.getUser1());
            }
        });
        return friendRequests;
    }

    //accept a friend request
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

    public ArrayList<User> getFriendsAndFriendRequestOfUser(String username) {
        ArrayList<User> friends = new ArrayList<>();
        User user = getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        getFriendsOfUser(username).forEach(friends::add);
        getFriendRequests(username).forEach(friends::add);
        friends.forEach(f -> System.out.println(f.getUsername()));
        return friends;
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

    public List<User> getPotentialFriend(String username) {
        ArrayList<User> potentialFriends = new ArrayList<>();

        //get all users that are not friends with the user and are not the user or do not have a pending friendship
        getAllUsers().forEach(user -> {
            System.out.println(getFriendshipStatus(username, user.getUsername())  +" " + user.getUsername());
            if (!user.getUsername().equals(username) && !getFriendsOfUser(username).contains(user) && getFriendshipStatus(username, user.getUsername()) == -1) {
                potentialFriends.add(user);
            }
        });
        for (User u : potentialFriends) {
            System.out.println(u.getUsername());
        }
        return potentialFriends;
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
