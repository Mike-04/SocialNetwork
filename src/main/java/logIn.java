import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.Service;

import java.io.IOException;

public class logIn {
    private String username;
    private Service service;

    @FXML
    private TextField usernamebox;

    public void setService(Service service) {
        this.service = service;
    }

    public void login() {
        username = usernamebox.getText();
        if (service.getUserByUsername(username) != null) {
            System.out.printf("Welcome to the FacePalm social network\n");
            System.out.printf("You are logged in as %s\n", username);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("loggedIn.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) usernamebox.getScene().getWindow();
                //set the service for the controller
                LoggedIn controller = loader.getController();
                controller.setUser(service.getUserByUsername(username));
                controller.setService(service);

                stage.setScene(new Scene(root));
                stage.setTitle("Logged In");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText("Username does not exist.");
            alert.showAndWait();
        }
    }
}

