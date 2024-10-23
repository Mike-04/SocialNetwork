package view;

import service.Service;

import java.util.Scanner;

public class ConsoleUI {
    private Service service;
    public ConsoleUI(Service service){
        this.service = service;
    }
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
                System.out.println("*face palms*");
            }
        }
    }
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
    void deleteUserUI(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Username:");
        String username = scanner.nextLine();
        service.deleteUser(username);
    }
    void addFriendshipUI(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Username 1:");
        String username1 = scanner.nextLine();
        System.out.println("Username 2:");
        String username2 = scanner.nextLine();
        service.addFriendship(username1,username2);
    }
    void removeFriendshipUI(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Username 1:");
        String username1 = scanner.nextLine();
        System.out.println("Username 2:");
        String username2 = scanner.nextLine();
        service.removeFriendship(username1,username2);
    }
    void printAllUsersUI(){
        service.getAllUsers().forEach(System.out::println);
    }
    void printAllFriendshipsUI(){
        service.getAllFriendships().forEach(System.out::println);
    }
    void printFriendsOfUserUI(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Username:");
        String username = scanner.nextLine();
        service.getFriendsOfUser(username).forEach(System.out::println);
    }
    void printBiggestCommunityUI(){
        service.getBiggestComunity().forEach(System.out::println);
    }
    void printNumberOfCommunitiesUI(){
        System.out.println(service.getNumberOfCommunities());
    }

}