package dbLayer.repositories;

import dbLayer.managers.DataAccessManager;
import entities.ProjectRequest;
import entities.ProjectType;
import entities.User;
import entities.UserStatus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectsRequestsRepository {

    private final Connection dbConnection;

    public ProjectsRequestsRepository(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public ProjectRequest convertResultSetToSingleObj(ResultSet resultSet) throws SQLException {

        resultSet.beforeFirst();
        if (!resultSet.next()) return new ProjectRequest();
        return convertResultSetToObj(resultSet);
    }

    private ProjectRequest convertResultSetToObj(ResultSet resultSet) throws SQLException {

        var obj = new ProjectRequest();
        obj.setId(resultSet.getInt("id"));
        obj.setName(resultSet.getString("name"));
        obj.setCost(resultSet.getFloat("cost"));
        obj.setComplexity(resultSet.getFloat("complexity"));
        obj.setDateOfIssue(resultSet.getTimestamp("dateOfIssue"));
        obj.setUser(new DataAccessManager(dbConnection).usersRepository.getById(resultSet.getInt("userId")));
        obj.setProjectType(new DataAccessManager(dbConnection).projectTypesRepository.getById(resultSet.getInt("projectTypeId")));
        return obj;
    }

    public List<ProjectRequest> convertResultSetToList(ResultSet resultSet) throws SQLException {

        var list = new ArrayList<ProjectRequest>();
        resultSet.beforeFirst();
        while (resultSet.next()) {

            list.add(convertResultSetToObj(resultSet));
        }
        return list;
    }

    private int getMaxId() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT MAX(id) from project_requests;");
        var resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public int create(ProjectRequest obj) throws SQLException {

        var insertStatement = dbConnection.prepareStatement(
                "INSERT INTO project_requests (projectTypeId, name, complexity, cost, dateOfIssue, userId) " +
                        "values (?, ?, ?, ?, ?, ?)");

        insertStatement.setInt(1, obj.getProjectType().getId());
        insertStatement.setString(2, obj.getName());
        insertStatement.setFloat(3, obj.getComplexity());
        insertStatement.setFloat(4, obj.getCost());
        insertStatement.setTimestamp(5, new Timestamp(obj.getDateOfIssue().getTime()));
        insertStatement.setInt(6, obj.getUser().getId());
        insertStatement.executeUpdate();
        return getMaxId();
    }

    public void update(ProjectRequest obj) throws SQLException {

        var updateStatement = dbConnection.prepareStatement(
                "UPDATE project_requests SET projectTypeId=?,name=?, complexity=?, cost=?, dateOfIssue=?, userId=?  where id = ?");
        updateStatement.setInt(1, obj.getProjectType().getId());
        updateStatement.setString(2, obj.getName());
        updateStatement.setFloat(3, obj.getComplexity());
        updateStatement.setFloat(4, obj.getCost());
        updateStatement.setTimestamp(5, new Timestamp(obj.getDateOfIssue().getTime()));
        updateStatement.setInt(6, obj.getUser().getId());
        updateStatement.setInt(7, obj.getId());
        updateStatement.executeUpdate();
    }

    public void delete(int id) throws SQLException {

        var deleteStatement = dbConnection.prepareStatement(
                "DELETE from project_requests where id=?");
        deleteStatement.setInt(1, id);
        deleteStatement.executeUpdate();
    }

    public ProjectRequest getById(int id) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM project_requests where id = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setInt(1, id);
        statement.executeQuery();
        return convertResultSetToSingleObj(statement.getResultSet());
    }

    public List<ProjectRequest> getAll() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM project_requests;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.executeQuery();
        return convertResultSetToList(statement.getResultSet());
    }

}
