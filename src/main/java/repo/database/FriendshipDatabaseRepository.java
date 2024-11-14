package repo.database;

import domain.Friendship;
import domain.User;
import domain.validators.FriendshipValidator;
import domain.validators.Validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class FriendshipDatabaseRepository extends AbstractDatabaseRepository<UUID, Friendship> {

    FriendshipValidator friendshipValidator;

    public FriendshipDatabaseRepository(Connection connection, Validator<Friendship> validator) {
        super(connection,validator);
        this.friendshipValidator = (FriendshipValidator) validator;
    }

    @Override
    public Optional<Friendship> findOne(UUID uuid) {
        if(uuid == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        //use prepared statement to get the user from the database
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM friendships WHERE id = ?");
            preparedStatement.setObject(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
            preparedStatement1.setObject(1, resultSet.getObject("user1"));
            ResultSet resultSet1 = preparedStatement1.executeQuery();
            User user1 = new User(resultSet1.getString("first_name"), resultSet1.getString("last_name"), resultSet1.getString("username"));
            user1.setId((UUID) resultSet1.getObject("id"));
            PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
            preparedStatement2.setObject(1, resultSet.getObject("user2"));
            ResultSet resultSet2 = preparedStatement2.executeQuery();
            User user2 = new User(resultSet2.getString("first_name"), resultSet2.getString("last_name"), resultSet2.getString("username"));
            Friendship friendship = new Friendship(user1, user2, resultSet.getTimestamp("date").toLocalDateTime(), resultSet.getInt("status"));
            friendship.setId((UUID) resultSet.getObject("id"));
            return Optional.of(friendship);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @Override
    public Iterable<Friendship> findAll() {
        ArrayList<Friendship> friendships = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM friendships");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                //get the first user
                PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
                preparedStatement1.setObject(1, resultSet.getObject("user1_id"));
                ResultSet resultSet1 = preparedStatement1.executeQuery();
                resultSet1.next();
                User user1 = new User(resultSet1.getString("first_name"), resultSet1.getString("last_name"), resultSet1.getString("username"));
                user1.setId((UUID) resultSet1.getObject("id"));
                //get the second user
                PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
                preparedStatement2.setObject(1, resultSet.getObject("user2_id"));
                ResultSet resultSet2 = preparedStatement2.executeQuery();
                resultSet2.next();
                User user2 = new User(resultSet2.getString("first_name"), resultSet2.getString("last_name"), resultSet2.getString("username"));
                user2.setId((UUID) resultSet2.getObject("id"));
                //create the friendship
                Friendship friendship = new Friendship(user1, user2, resultSet.getTimestamp("friendship_date").toLocalDateTime(), resultSet.getInt("status"));
                friendship.setId((UUID) resultSet.getObject("id"));
                friendships.add(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        System.out.println(entity);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE friendships SET user1_id = ?, user2_id = ?, friendship_date = ? , status = ? WHERE id = ?");
            preparedStatement.setObject(1, entity.getUser1().getId());
            preparedStatement.setObject(2, entity.getUser2().getId());
            preparedStatement.setObject(3, entity.getFriendshipDate());
            preparedStatement.setObject(4, entity.getStatus());
            preparedStatement.setObject(5, entity.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            return Optional.ofNullable(entity);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        friendshipValidator.validate(entity);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO friendships (id, user1_id, user2_id, friendship_date,status) VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setObject(1, entity.getId());
            preparedStatement.setObject(2, entity.getUser1().getId());
            preparedStatement.setObject(3, entity.getUser2().getId());
            preparedStatement.setObject(4, entity.getFriendshipDate());
            preparedStatement.setObject(5, entity.getStatus());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            return Optional.ofNullable(entity);
        }
        return Optional.empty();

    }

    @Override
    public Optional<Friendship> delete(UUID uuid) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM friendships WHERE id = ?");
            preparedStatement.setObject(1, uuid);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            return Optional.empty();
        }
        return Optional.empty();

    }
}
