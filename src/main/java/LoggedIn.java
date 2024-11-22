import domain.Friendship;
import domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import service.Controller;
import service.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggedIn {

    private Service service;
    private User user;

    @FXML
    private TableView<User> friendstable;
    @FXML
    private TableColumn<User, String> firstNameColumn;
    @FXML
    private TableColumn<User, String> lastNameColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, Void> actionsColumn;

    @FXML
    private TextField searchField;  // The search field for searching users
    @FXML
    private TableView<User> searchResultsTable;  // Table for displaying search results
    @FXML
    private TableColumn<User, String> usernameColumnSearch;
    @FXML
    private TableColumn<User, String> firstNameColumnSearch;
    @FXML
    private TableColumn<User, String> lastNameColumnSearch;
    @FXML
    private TableColumn<User, Void> actionsColumnSearch;
    @FXML
    private TableColumn<User, Void> dateColumn;


    public void setUser(User user) {
        this.user = user;
    }

    public void setService(Service service) {
        this.service = service;
        populateFriendsTable();
        populateSearchTable();
        handleSearch();
    }

    public void deleteAccount() {
        service.deleteUser(user.getUsername());
        // Close the window
        System.exit(0);
    }

    public void logout() {
        //return to the login page
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("logIn.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) friendstable.getScene().getWindow();
            logIn controller = loader.getController();
            controller.setService(service);
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
    }
    }

    public void populateFriendsTable() {
        // Set up cell value factories
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        //populate the date column with the date of the friendship between the user and the friend
        dateColumn.setCellFactory(new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
            @Override
            public TableCell<User, Void> call(TableColumn<User, Void> param) {
                return new TableCell<User, Void>() {
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        //get friendship date from the service
                        if (empty) {
                            setText(null);
                        } else {
                            //format the date
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            User currentUser = getTableView().getItems().get(getIndex());
                            LocalDateTime friendshipDate = service.getFriendshipDate(user.getUsername(), currentUser.getUsername());
                            setText(friendshipDate.format(formatter).toString());
                        }

                    }
                };
            }
        });

        // Set up the actions column to have buttons
        actionsColumn.setCellFactory(new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
            @Override
            public TableCell<User, Void> call(TableColumn<User, Void> param) {
                return new TableCell<User, Void>() {
                    private final Button removeButton = new Button("Remove");
                    private final Button acceptButton = new Button("Accept");
                    private final Button denyButton = new Button("Deny");
                    private final Button cancelButton = new Button("Cancel..");

                    {
                        // When the remove button is clicked, remove the friend
                        removeButton.setOnAction(event -> {
                            User selectedUser = getTableView().getItems().get(getIndex());
                            removeFriend(selectedUser);
                        });

                        // When the accept button is clicked, accept the friend request
                        acceptButton.setOnAction(event -> {
                            User selectedUser = getTableView().getItems().get(getIndex());
                            acceptFriend(selectedUser);
                        });

                        // When the deny button is clicked, deny the friend request
                        denyButton.setOnAction(event -> {
                            User selectedUser = getTableView().getItems().get(getIndex());
                            denyFriend(selectedUser);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            User currentUser = getTableView().getItems().get(getIndex());
                            System.out.println(service.getFriendshipSender(user.getUsername(), currentUser.getUsername()));
                            if (service.getFriendshipStatus(user.getUsername(), currentUser.getUsername())==0) {
                                setGraphic(new HBox(acceptButton, denyButton));
                            //display a cancel friend request button if the friendship status is 0 and the current user is the user who sent the request

                            } else {
                                setGraphic(removeButton);
                            }
                        }
                    }
                };
            }
        });

        // Load data into the table
        ObservableList<User> friends = FXCollections.observableArrayList(service.getFriendsAndFriendRequestOfUser(user.getUsername()));


        friendstable.setItems(friends);
    }

    private void removeFriend(User friend) {
        // Remove the friend using your service
        service.removeFriendship(user.getUsername(), friend.getUsername());

        // Refresh the table after removal
        populateFriendsTable();
        handleSearch();
    }

    private void acceptFriend(User friend) {
        // Accept the friend using your service
        System.out.println(user.getUsername()+","+ friend.getUsername());
        service.acceptFriendRequest(user.getUsername(), friend.getUsername());

        // Refresh the table after removal
        populateFriendsTable();
    }

    private void denyFriend(User friend) {
        // Deny the friend using your service
        service.removeFriendship(user.getUsername(), friend.getUsername());

        // Refresh the table after removal
        populateFriendsTable();
    }


    @FXML
    public void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            //if field is empty, load all potential friends
            ObservableList<User> allUsers = FXCollections.observableArrayList(service.getPotentialFriend(user.getUsername()));
            searchResultsTable.setItems(allUsers);
        } else {
            // Perform search by filtering users based on the search text
            ObservableList<User> searchResults = FXCollections.observableArrayList();
            for (User potentialFriend : service.getPotentialFriend(user.getUsername())) {
                if (potentialFriend.getUsername().toLowerCase().contains(searchText) ||
                        potentialFriend.getFirstName().toLowerCase().contains(searchText) ||
                        potentialFriend.getLastName().toLowerCase().contains(searchText)) {
                    searchResults.add(potentialFriend);
                }
            }
            // Set the search results in the TableView
            searchResultsTable.setItems(searchResults);
        }
    }

    private void loadAllUsers() {
        // Load all users into the TableView
        ObservableList<User> allUsers = FXCollections.observableArrayList(service.getAllUsers());
        searchResultsTable.setItems(allUsers);
    }

    public void populateSearchTable() {
        // Set up the table columns to display user information
        firstNameColumnSearch.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumnSearch.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        usernameColumnSearch.setCellValueFactory(new PropertyValueFactory<>("username"));

        // Set up the actions column to display "Send Request" button
        actionsColumnSearch.setCellFactory(new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
            @Override
            public TableCell<User, Void> call(TableColumn<User, Void> param) {
                return new TableCell<User, Void>() {
                    private final Button sendRequestButton = new Button("Friend");

                    {
                        // When the button is clicked, send a friend request to the user
                        sendRequestButton.setOnAction(event -> {
                            User selectedUser = getTableView().getItems().get(getIndex());
                            sendFriendRequest(selectedUser);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(new HBox(10, sendRequestButton));
                        }
                    }
                };
            }
        });
    }

    private void sendFriendRequest(User friend) {
        // Send a friend request to the selected user
        service.addFriendship(user.getUsername(), friend.getUsername());

        // Optionally, you can refresh the table after sending a request
        handleSearch();  // Re-filter the search table
    }
}
