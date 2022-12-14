package dbLayer.repositories;

import dbLayer.managers.DataAccessManager;
import entities.Request;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RequestsRepository {

    private final Connection dbConnection;

    public RequestsRepository(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public Request convertResultSetToSingleObj(ResultSet resultSet) throws SQLException {

        resultSet.beforeFirst();
        if (!resultSet.next()) return new Request();
        return convertResultSetToObj(resultSet);
    }

    private Request convertResultSetToObj(ResultSet resultSet) throws SQLException {

        var obj = new Request();
        obj.setId(resultSet.getInt("id"));
        obj.setDescription(resultSet.getString("description"));
        obj.setPassedSoft(resultSet.getInt("isPassedSoftExam") == 1);
        obj.setPassedHard(resultSet.getInt("isPassedHardExam") == 1);
        obj.setPassedEnglish(resultSet.getInt("isPassedEnglishExam") == 1);
        obj.setDateOfIssue(new Date(resultSet.getTimestamp("date").getTime()));
        obj.setUser(new DataAccessManager(dbConnection).usersRepository.getById(resultSet.getInt("userId")));
        obj.setPosition(new DataAccessManager(dbConnection).positionsRepository.getById(resultSet.getInt("positionId")));
        return obj;
    }

    public List<Request> convertResultSetToList(ResultSet resultSet) throws SQLException {

        var list = new ArrayList<Request>();
        resultSet.beforeFirst();
        while (resultSet.next()) {

            list.add(convertResultSetToObj(resultSet));
        }
        return list;
    }

    private int getMaxId() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT MAX(id) from requests;");
        var resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public int create(Request obj) throws SQLException {

        var insertStatement = dbConnection.prepareStatement(
                "INSERT INTO requests (positionId, userId, description, date, isPassedSoftExam, isPassedHardExam, isPassedEnglishExam) " +
                        "values (?, ?, ?, ?, ?, ?, ?)");

        insertStatement.setInt(1, obj.getPosition().getId());
        insertStatement.setInt(2, obj.getUser().getId());
        insertStatement.setString(3, obj.getDescription());
        insertStatement.setTimestamp(4, new Timestamp(obj.getDateOfIssue().getTime()));
        insertStatement.setInt(5, obj.isPassedSoft()? 1:0);
        insertStatement.setInt(6, obj.isPassedHard()? 1:0);
        insertStatement.setInt(7, obj.isPassedEnglish()? 1:0);
        insertStatement.executeUpdate();
        return getMaxId();
    }

    public void update(Request obj) throws SQLException {

        var updateStatement = dbConnection.prepareStatement(
                "UPDATE requests SET positionId=?,userId=?, description=?, date=?, isPassedSoftExam=?, isPassedHardExam=?, isPassedEnglishExam=?  where id = ?");
        updateStatement.setInt(1, obj.getPosition().getId());
        updateStatement.setInt(2, obj.getUser().getId());
        updateStatement.setString(3, obj.getDescription());
        updateStatement.setTimestamp(4, new Timestamp(obj.getDateOfIssue().getTime()));
        updateStatement.setInt(5, obj.isPassedSoft()? 1:0);
        updateStatement.setInt(6, obj.isPassedHard()? 1:0);
        updateStatement.setInt(7, obj.isPassedEnglish()? 1:0);
        updateStatement.setInt(8, obj.getId());
        updateStatement.executeUpdate();
    }

    public void delete(int id) throws SQLException {

        var deleteStatement = dbConnection.prepareStatement(
                "DELETE from requests where id=?");
        deleteStatement.setInt(1, id);
        deleteStatement.executeUpdate();
    }

    public Request getById(int id) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM requests where id = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setInt(1, id);
        statement.executeQuery();
        return convertResultSetToSingleObj(statement.getResultSet());
    }

    public List<Request> getAll() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM requests WHERE id NOT IN (SELECT requestId FROM employee);",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.executeQuery();
        return convertResultSetToList(statement.getResultSet());
    }

}
