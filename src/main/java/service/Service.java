package service;

import domain.Entity;
import domain.Friendship;
import domain.User;
import repo.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class Service implements Controller{

    private Repository<UUID,User> userRepo;
    private Repository<UUID,Friendship> friendshipRepo;

    /**
     * @param userRepo the repository of users
     * @param friendshipRepo the repository of friendships
     *                       Creates a new Service object
     */
    public Service(Repository<UUID, User> userRepo, Repository<UUID, Friendship> friendshipRepo){
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
    }

    /**
     * @param username the username of the user
     * @return the friends of the user with the given username
     *        Throws RuntimeException if the user does not exist
     *        Returns an ArrayList of User objects
     */
    public ArrayList<User> getFriendsOfUser(String username){
        User u = (User) this.getUserByUsername(username);
        if(u == null){
            throw new RuntimeException("User does not exist");
        }
        return new ArrayList<>(u.getFriends());
    }

    /**
     * @param username the username of the user
     * @return the user with the given username
     *       Returns null if the user does not exist
     *       Returns an Entity object
     */
    protected Entity<UUID> getUserByUsername(String username){
        //get all users
        //return the user with the given username
        Iterable<User> users = userRepo.findAll();
        for(User u : users){
            if(u.getUsername().equals(username)){
                return u;
            }
        }

        return null;
    }

    /**
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @param username the username of the user
     *                 Adds a new user to the social network
     *                 Throws RuntimeException if the user already exists
     *                 Throws RuntimeException if the user cannot be saved
     *                Returns nothing
     */
    @Override
    public void addUser(String firstName, String lastName, String username) {
        try{
            if(this.getUserByUsername(username) != null){
                throw new Exception("User already exists");
            }
            userRepo.save(new User(firstName,lastName,username));
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @param username the username of the user
     *                 Deletes a user from the social network
     *                 Throws RuntimeException if the user does not exist
     *                 Returns nothing
     */
    @Override
    public void deleteUser(String username) {
        try{
            User u = (User) this.getUserByUsername(username);
            if(u == null){
                throw new Exception("User does not exist");
            }
            userRepo.delete(u.getId());
            //remove friendships
            Iterable<Friendship> friendships = friendshipRepo.findAll();
            HashSet<Friendship> toDelete = new HashSet<>();
            for(Friendship f : friendships){
                if(f.getUser1().equals(u) || f.getUser2().equals(u)){
                    toDelete.add(f);
                }
            }

            for(Friendship f : toDelete){
                friendshipRepo.delete(f.getId());
            }

            //remove the user from all user friend lists
            for(User user : userRepo.findAll()){
                user.removeFriend(u);
            }
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @param username1 the username of the first user
     * @param username2 the username of the second user
     *                  Adds a friendship between two users
     */
    @Override
    public void addFriendship(String username1, String username2) {
        try{
            User u1 = (User) this.getUserByUsername(username1);
            User u2 = (User) this.getUserByUsername(username2);
            if(u1 == null || u2 == null){
                throw new Exception("One or both users do not exist");
            }
            friendshipRepo.save(new Friendship(u1,u2));
            u1.addFriend(u2);
            u2.addFriend(u1);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @param username1 the username of the first user
     * @param username2 the username of the second user
     *                  Removes a friendship between two users
     */
    @Override
    public void removeFriendship(String username1, String username2) {
        try{
            User u1 = (User) this.getUserByUsername(username1);
            User u2 = (User) this.getUserByUsername(username2);
            if(u1 == null || u2 == null){
                throw new Exception("One or both users do not exist");
            }
            Iterable<Friendship> friendships = friendshipRepo.findAll();
            for (Friendship f : friendships){
                if(f.getUser1().equals(u1) && f.getUser2().equals(u2) || f.getUser1().equals(u2) && f.getUser2().equals(u1)){
                    friendshipRepo.delete(f.getId());
                    u1.removeFriend(u2);
                    u2.removeFriend(u1);
                    return;
                }
            }
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }

    /**
     * @return all users in the social network
     *       Returns an ArrayList of User objects
     */
    @Override
    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        for(User u : userRepo.findAll()){
            users.add(u);
        }
        return users;
    }

    /**
     * @return all friendships in the social network
     *      Returns an ArrayList of Friendship objects
     */
    @Override
    public ArrayList<Friendship> getAllFriendships() {
        ArrayList<Friendship> friendships = new ArrayList<>();
        for(Friendship f : friendshipRepo.findAll()){
            friendships.add(f);
        }
        return friendships;
    }

    /**
     * @return the biggest community in the social network
     *     Returns an ArrayList of User objects
     *     The biggest community is the connected component with the most users
     */
    @Override
    public ArrayList<User> getBiggestComunity() {
        //get all connected components in the friendship graph
        Iterable<User> users = userRepo.findAll();
        ArrayList<User> visited = new ArrayList<>();
        ArrayList<User> biggestCommunity = new ArrayList<>();
        int max = 0;
        for(User u : users){
            if(!visited.contains(u)){
                ArrayList<User> connectedComponent = dfs(u,visited);
                if(connectedComponent.size() > max){
                    max = connectedComponent.size();
                    biggestCommunity = connectedComponent;
                }
            }
        }
        return biggestCommunity;
    }

    /**
     * @return the number of communities in the social network
     *   Returns an integer
     */
    @Override
    public int getNumberOfCommunities() {
        //find the number of connected components in the friendship graph
        Iterable<User> users = userRepo.findAll();
        ArrayList<User> visited = new ArrayList<>();
        int count = 0;
        for(User u : users){
            if(!visited.contains(u)){
                count++;
                ArrayList<User> connectedComponent = dfs(u,visited);
            }
        }
        return count;
    }

    /**
     * @param u the user
     * @param visited the list of visited users
     * @return the connected component of the user
     *      Returns an ArrayList of User objects
     */
    private ArrayList<User> dfs(User u, ArrayList<User> visited) {
        ArrayList<User> connectedComponent = new ArrayList<>();

        // Mark the current user as visited and add to the current component
        visited.add(u);
        connectedComponent.add(u);

        // Explore friends
        for (User friend : u.getFriends()) {
            if (!visited.contains(friend)) {
                // Print the friend
                // Recursively visit friends of the current user
                ArrayList<User> list = dfs(friend, visited);
                connectedComponent.addAll(list);  // Add all the friends found
            }
        }
        return connectedComponent;
    }

}
