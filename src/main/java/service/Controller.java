package service;

import domain.Friendship;
import domain.User;

import java.util.ArrayList;

/**
 * Controller interface
 * Contains the methods that the controller should implement
 * The controller is the intermediary between the user interface and the repository
 */
public interface Controller {
    void addUser(String firstName, String lastName, String username);
    void deleteUser(String username);
    void addFriendship(String username1, String username2);
    void removeFriendship(String username1, String username2);
    ArrayList<User> getAllUsers();
    ArrayList<Friendship> getAllFriendships();
    ArrayList<User> getBiggestComunity();
    int getNumberOfCommunities();

}
