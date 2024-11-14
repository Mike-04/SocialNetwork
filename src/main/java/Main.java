import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import domain.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import repo.database.FriendshipDatabaseRepository;
import repo.database.UserDatabaseRepository;
import repo.file.*;
import repo.*;
import domain.validators.*;
import service.*;
import view.*;

public class Main extends Application {

    private static final Log log = LogFactory.getLog(Main.class);

    public static Connection getConcetion() {
        //get the database connection info from the file database.properties
        java.util.Properties properties = new java.util.Properties();
        try {
            properties.load(new FileInputStream("database.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");
        System.out.println("Trying to connect to the database");
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);



    }

    @Override
    public void start(Stage stage) throws Exception {
        // do some testing here
        Connection connection = getConcetion();
        System.out.println("Connected to the database");
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
//        Repository<UUID, User> userRepo = new UserFileRepo("users.txt", userValidator);
//        Repository<UUID, Friendship> friendshipRepo = new FriendshipFileRepo("friendships.txt", friendshipValidator, userRepo);
        Repository<UUID, User> userRepo = new UserDatabaseRepository(connection, userValidator);
        Repository<UUID, Friendship> friendshipRepo = new FriendshipDatabaseRepository(connection, friendshipValidator);
        // print all friendships
        Service service = new Service(userRepo, friendshipRepo);
        //print all the friends of all users
        initView(stage);
        stage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader stageLoader = new FXMLLoader();
        stageLoader.setLocation(getClass().getResource("logIn.fxml"));
        AnchorPane layout = stageLoader.load();
        primaryStage.setScene(new Scene(layout));
        primaryStage.setTitle("Social Network");
        primaryStage.show();
    }
}
