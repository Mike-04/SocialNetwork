package view;

import service.Service;

import java.util.Scanner;

/**
 * Console user interface
 * Contains the methods for the console user interface
 */
public class ConsoleUI {
    private final Service service;

    /**
     * @param service the service to be used
     *          Creates a new ConsoleUI object
     */
    public ConsoleUI(Service service){
        this.service = service;
    }

    /**
     *         Starts the console user interface
     */
    public void start(){
        Scanner scanner = new Scanner(System.in);
        while(true){
            try{
            System.out.println("Welcome to the FacePalm social network");
            System.out.println("1. Add user");
            System.out.println("2. Delete user");
            System.out.println("3. Add friendship");
            System.out.println("4. Delete friendship");
            System.out.println("5. Print all users");
            System.out.println("6. Print all friendships");
            System.out.println("7. Print all friends of a user");
            System.out.println("8. Print the biggest community");
            System.out.println("9. Print the number of communities");
            System.out.println("0. Exit");
            int option = scanner.nextInt();
            scanner.nextLine();
            switch(option) {
                case 1:
                    this.addUserUI();
                    break;
                case 2:
                    this.deleteUserUI();
                    break;
                case 3:
                    this.addFriendshipUI();
                    break;
                case 4:
                    this.removeFriendshipUI();
                    break;
                case 5:
                    this.printAllUsersUI();
                    break;
                case 6:
                    this.printAllFriendshipsUI();
                    break;
                case 7:
                    this.printFriendsOfUserUI();
                    break;
                case 8:
                    this.printBiggestCommunityUI();
                    break;
                case 9:
                    this.printNumberOfCommunitiesUI();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("*face palms*\nYou can't read?");
            }
            }catch(Exception e){
                System.out.println("*face palms* "+e.getMessage());
            }
        }
    }

    /**
     *        Adds a user to the social network
     */
    void addUserUI(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("First name:");
        String firstName = scanner.nextLine();
        System.out.println("Last name:");
        String lastName = scanner.nextLine();
        System.out.println("Username:");
        String username = scanner.nextLine();
        service.addUser(firstName,lastName,username);
    }

    /**
     *        Deletes a user from the social network
     */
    void deleteUserUI(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Username:");
        String username = scanner.nextLine();
        service.deleteUser(username);
    }

    /**
     *       Adds a friendship between two users
     */
    void addFriendshipUI(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Username 1:");
        String username1 = scanner.nextLine();
        System.out.println("Username 2:");
        String username2 = scanner.nextLine();
        service.addFriendship(username1,username2);
    }

    /**
     *       Removes a friendship between two users
     */
     void removeFriendshipUI(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Username 1:");
        String username1 = scanner.nextLine();
        System.out.println("Username 2:");
        String username2 = scanner.nextLine();
        service.removeFriendship(username1,username2);
    }

    /**
     *     Prints all users in the social network
     */
    void printAllUsersUI(){
        service.getAllUsers().forEach(System.out::println);
    }

    /**
     *    Prints all friendships in the social network
     */
    void printAllFriendshipsUI(){
        service.getAllFriendships().forEach(System.out::println);
    }

    /**
     *   Prints all friends of a user
     */
    void printFriendsOfUserUI(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Username:");
        String username = scanner.nextLine();
        service.getFriendsOfUser(username).forEach(System.out::println);
    }

    /**
     *   Prints the biggest community in the social network
     */
    void printBiggestCommunityUI(){
        service.getBiggestCommunity().forEach(System.out::println);
    }

    /**
     *  Prints the number of communities in the social network
     */
    void printNumberOfCommunitiesUI(){
        System.out.println(service.getNumberOfCommunities());
    }

}


