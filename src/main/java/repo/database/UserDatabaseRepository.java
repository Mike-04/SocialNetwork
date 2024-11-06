package repo.database;

import domain.User;
import domain.validators.Validator;
import repo.Repository;

import java.awt.geom.Area;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.SplittableRandom;
import java.util.UUID;

public class UserDatabaseRepository extends AbstractDatabaseRepository<UUID, User> {

    public UserDatabaseRepository(Connection connection, Validator<User> validator) {
        super(connection,validator);
    }

    @Override
    public Optional<User> findOne(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        String userQuery = "SELECT * FROM users WHERE id = ?";
        String friendshipsQuery = "SELECT u.* FROM friendships f " +
                "JOIN users u ON (f.user1_id = u.id OR f.user2_id = u.id) " +
                "WHERE (f.user1_id = ? OR f.user2_id = ?) AND u.id <> ?";

        try (PreparedStatement userStatement = connection.prepareStatement(userQuery)) {
            userStatement.setObject(1, uuid);
            try (ResultSet userResultSet = userStatement.executeQuery()) {
                if (!userResultSet.next()) {
                    // Return empty Optional if user with given UUID is not found
                    return Optional.empty();
                }

                // Create the User object from the result set
                User user = new User(userResultSet.getString("first_name"),
                        userResultSet.getString("last_name"),
                        userResultSet.getString("username"));
                user.setId((UUID) userResultSet.getObject("id"));

                // Now retrieve friendships for this user
                try (PreparedStatement friendshipsStatement = connection.prepareStatement(friendshipsQuery)) {
                    friendshipsStatement.setObject(1, user.getId());
                    friendshipsStatement.setObject(2, user.getId());
                    friendshipsStatement.setObject(3, user.getId()); // Exclude the user themselves from friendships

                    try (ResultSet friendsResultSet = friendshipsStatement.executeQuery()) {
                        while (friendsResultSet.next()) {
                            // Create a friend User object from the result set
                            User friend = new User(friendsResultSet.getString("first_name"),
                                    friendsResultSet.getString("last_name"),
                                    friendsResultSet.getString("username"));
                            friend.setId((UUID) friendsResultSet.getObject("id"));
                            user.addFriend(friend);  // Assuming addFriend method is available in User
                        }
                    }
                }
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }


    @Override
    public Iterable<User> findAll() {
        ArrayList<User> users = new ArrayList<>();
        String userQuery = "SELECT * FROM users";
        String friendshipsQuery = "SELECT u.* FROM friendships f " +
                "JOIN users u ON (f.user1_id = u.id OR f.user2_id = u.id) " +
                "WHERE (f.user1_id = ? OR f.user2_id = ?) AND u.id <> ?";

        try (PreparedStatement userStatement = connection.prepareStatement(userQuery);
             ResultSet userResultSet = userStatement.executeQuery()) {

            while (userResultSet.next()) {
                // Create a User object from the result set
                User user = new User(userResultSet.getString("first_name"),
                        userResultSet.getString("last_name"),
                        userResultSet.getString("username"));
                user.setId((UUID) userResultSet.getObject("id"));
//                System.out.println(user);

                // Now, fetch friendships for this user
                try (PreparedStatement friendshipsStatement = connection.prepareStatement(friendshipsQuery)) {
                    friendshipsStatement.setObject(1, user.getId());
                    friendshipsStatement.setObject(2, user.getId());
                    friendshipsStatement.setObject(3, user.getId());  // Exclude the user from their own friendships

                    try (ResultSet friendsResultSet = friendshipsStatement.executeQuery()) {
                        while (friendsResultSet.next()) {
                            // Create a friend User object from the result set
                            User friend = new User(friendsResultSet.getString("first_name"),
                                    friendsResultSet.getString("last_name"),
                                    friendsResultSet.getString("username"));
                            friend.setId((UUID) friendsResultSet.getObject("id"));
                            user.addFriend(friend);  // Assuming `addFriend` is a method to add a friend to a user
                        }
                    }
                }
                users.add(user);  // Add user with friendships to the list
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        //for every user swap the friend objects with the actual user objects
        for (User user : users) {
            ArrayList<User> friends = new ArrayList<>();
            for (User friend : user.getFriends()) {
                for (User u : users) {
                    if (u.getId().equals(friend.getId())) {
                        friends.add(u);
                    }
                }
            }
            user.setFriends(friends);
        }
        return users;
    }


    @Override
    public Optional<User> update(User entity) {
        //use prepared statement to update the user in the database
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET first_name = ?, last_name = ?, username = ? WHERE id = ?");
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setString(3, entity.getUsername());
            preparedStatement.setObject(4, entity.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            return Optional.ofNullable(entity);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> save(User entity) {
        //use prepared statement to insert the user into the database
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users VALUES (?, ?, ?, ?)");
            preparedStatement.setObject(1, entity.getId());
            preparedStatement.setString(2, entity.getFirstName());
            preparedStatement.setString(3, entity.getLastName());
            preparedStatement.setString(4, entity.getUsername());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            return Optional.ofNullable(entity);
    }
        return Optional.empty();
    }

    @Override
    public Optional<User> delete(UUID uuid) {
        //use prepared statement to delete the user from the database
        try {
            Optional<User> user = findOne(uuid);
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM users WHERE id = ?");
            preparedStatement.setObject(1, uuid);
            preparedStatement.executeUpdate();

            System.out.println("User deleted: " + user);
            return user;
        } catch (SQLException e) {
            return Optional.empty();
        }
    }
}
