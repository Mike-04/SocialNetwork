import domain.User;
import service.Service;

public class LoggedIn {
    private Service service;
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public void setService(Service service) {
        this.service = service;
        //print the user's friends
        System.out.println("Your friends are:");
        service.getFriendsOfUser(user.getUsername()).forEach(System.out::println);
        //print the users friend requests
        System.out.println("Your friend requests are:");
        service.getFriendRequests(user.getUsername()).forEach(System.out::println);

        //accept a friend request
        service.acceptFriendRequest(user.getUsername(), "stefang");

        System.out.println("Your friends are:");
        service.getFriendsOfUser(user.getUsername()).forEach(System.out::println);
        //print the users friend requests
        System.out.println("Your friend requests are:");
        service.getFriendRequests(user.getUsername()).forEach(System.out::println);
        //print the friends of the friend
        System.out.println("The friends of your friend are:");
        service.getUserByUsername("stefang").getFriends().forEach(System.out::println);

    }






}
