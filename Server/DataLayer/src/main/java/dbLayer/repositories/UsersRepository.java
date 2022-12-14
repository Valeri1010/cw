package dbLayer.repositories;

import entities.User;
import entities.UserStatus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersRepository {

    private final Connection dbConnection;

    public UsersRepository(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    protected static User convertResultSetToSingleObj(ResultSet resultSet) throws SQLException {

        resultSet.beforeFirst();
        if (!resultSet.next()) return new User();
        return convertResultSetToObj(resultSet);
    }

    private static User convertResultSetToObj(ResultSet resultSet) throws SQLException {

        var obj = new User();
        obj.setId(resultSet.getInt("users.id"));
        obj.setLogin(resultSet.getString("login"));
        obj.setPassword(resultSet.getString("password"));
        switch (resultSet.getInt("status")) {
            case 0 -> obj.setStatus(UserStatus.NOT_BANNED);
            case 1 -> obj.setStatus(UserStatus.BANNED);
            default -> obj.setStatus(UserStatus.BANNED);
        }
        obj.setFullName(resultSet.getString("fullName"));
        obj.setExperience(resultSet.getInt("expirience"));
        return obj;
    }

    protected static List<User> convertResultSetToList(ResultSet resultSet) throws SQLException {

        var list = new ArrayList<User>();
        resultSet.beforeFirst();
        while (resultSet.next()) {

            list.add(convertResultSetToObj(resultSet));
        }
        return list;
    }

    private int getMaxId() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT MAX(id) from users;");
        var resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public int create(User obj) throws SQLException {

        var insertStatement = dbConnection.prepareStatement(
                "INSERT INTO users (login, password, status, fullName, expirience) " +
                        "values (?, ?, ?, ?, ?)");

        insertStatement.setString(1, obj.getLogin());
        insertStatement.setString(2, obj.getPassword());
        insertStatement.setInt(3, obj.getStatus().ordinal());
        insertStatement.setString(4, obj.getFullName());
        insertStatement.setInt(5, obj.getExperience());
        insertStatement.executeUpdate();
        return getMaxId();
    }

    public void update(User obj) throws SQLException {

        var updateStatement = dbConnection.prepareStatement(
                "UPDATE users SET login=?, password=?, status=?, fullName=?, expirience=?  where id = ?");
        updateStatement.setString(1, obj.getLogin());
        updateStatement.setString(2, obj.getPassword());
        updateStatement.setInt(3, obj.getStatus().ordinal());
        updateStatement.setString(4, obj.getFullName());
        updateStatement.setInt(5, obj.getExperience());
        updateStatement.setInt(6, obj.getId());
        updateStatement.executeUpdate();
    }

    public void delete(int id) throws SQLException {

        var deleteStatement = dbConnection.prepareStatement(
                "DELETE from users where id=?");
        deleteStatement.setInt(1, id);
        deleteStatement.executeUpdate();
    }

    public User getById(int id) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT users.id, login, password, status, fullName, expirience" +
                        " FROM users where id = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setInt(1, id);
        statement.executeQuery();
        return convertResultSetToSingleObj(statement.getResultSet());
    }

    public User get(String login, String password) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT users.id, login, password, status, fullName, expirience" +
                        " FROM users where login = ? AND password = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setString(1, login);
        statement.setString(2, password);
        statement.executeQuery();
        return convertResultSetToSingleObj(statement.getResultSet());
    }

    public User get(String login) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT users.id, login, password, status, fullName, expirience" +
                        " FROM users where login = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setString(1, login);
        statement.executeQuery();
        return convertResultSetToSingleObj(statement.getResultSet());
    }

    public List<User> getAll() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT users.id, login, password, status, fullName, expirience" +
                        " FROM users;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.executeQuery();
        return convertResultSetToList(statement.getResultSet());
    }
}
