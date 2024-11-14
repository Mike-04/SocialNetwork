package repo.database;

import domain.User;
import domain.Friendship;
import domain.validators.Validator;
import repo.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class UserDatabaseRepository extends AbstractDatabaseRepository<UUID, User> {

    private final Repository<UUID, Friendship> friendshipRepo;

    public UserDatabaseRepository(Connection connection, Validator<User> validator, Repository<UUID, Friendship> friendshipRepo) {
        super(connection, validator);
        this.friendshipRepo = friendshipRepo;
    }

    @Override
    public Optional<User> findOne(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        String userQuery = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement userStatement = connection.prepareStatement(userQuery)) {
            userStatement.setObject(1, uuid);
            try (ResultSet userResultSet = userStatement.executeQuery()) {
                if (!userResultSet.next()) {
                    return Optional.empty();
                }

                User user = new User(userResultSet.getString("first_name"),
                        userResultSet.getString("last_name"),
                        userResultSet.getString("username"));
                user.setId((UUID) userResultSet.getObject("id"));

                friendshipRepo.findAll().forEach(friendship -> {
                    if (friendship.getStatus() == 1 && (friendship.getUser1().getId().equals(user.getId()) || friendship.getUser2().getId().equals(user.getId()))) {
                        User friend = friendship.getUser1().getId().equals(user.getId()) ? friendship.getUser2() : friendship.getUser1();
                        user.addFriend(friend);
                    }
                });

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

        try (PreparedStatement userStatement = connection.prepareStatement(userQuery);
             ResultSet userResultSet = userStatement.executeQuery()) {

            while (userResultSet.next()) {
                User user = new User(userResultSet.getString("first_name"),
                        userResultSet.getString("last_name"),
                        userResultSet.getString("username"));
                user.setId((UUID) userResultSet.getObject("id"));
                users.add(user);
            }

            //add the friends to the users
            friendshipRepo.findAll().forEach(friendship -> {
                for (User user : users) {
                    if (friendship.getStatus() == 1 && (friendship.getUser1().getId().equals(user.getId()) || friendship.getUser2().getId().equals(user.getId()))) {
                        User friend = friendship.getUser1().getId().equals(user.getId()) ? friendship.getUser2() : friendship.getUser1();
                        user.addFriend(friend);
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        // replace the friend ids with the actual friends
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
        try {
            Optional<User> user = findOne(uuid);
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM users WHERE id = ?");
            preparedStatement.setObject(1, uuid);
            preparedStatement.executeUpdate();

            return user;
        } catch (SQLException e) {
            return Optional.empty();
        }
    }
}