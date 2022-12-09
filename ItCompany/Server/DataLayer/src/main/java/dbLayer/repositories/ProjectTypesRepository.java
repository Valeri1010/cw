package dbLayer.repositories;

import entities.ProjectType;
import entities.User;
import entities.UserStatus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectTypesRepository {

    private final Connection dbConnection;

    public ProjectTypesRepository(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    protected static ProjectType convertResultSetToSingleObj(ResultSet resultSet) throws SQLException {

        resultSet.beforeFirst();
        if (!resultSet.next()) return new ProjectType();
        return convertResultSetToObj(resultSet);
    }

    private static ProjectType convertResultSetToObj(ResultSet resultSet) throws SQLException {

        var obj = new ProjectType();
        obj.setId(resultSet.getInt("project_types.id"));
        obj.setName(resultSet.getString("project_types.name"));
        return obj;
    }

    protected static List<ProjectType> convertResultSetToList(ResultSet resultSet) throws SQLException {

        var list = new ArrayList<ProjectType>();
        resultSet.beforeFirst();
        while (resultSet.next()) {

            list.add(convertResultSetToObj(resultSet));
        }
        return list;
    }

    private int getMaxId() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT MAX(id) from project_types;");
        var resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public int create(ProjectType obj) throws SQLException {

        var insertStatement = dbConnection.prepareStatement(
                "INSERT INTO project_types (name) " +
                        "values (?)");

        insertStatement.setString(1, obj.getName());
        insertStatement.executeUpdate();
        return getMaxId();
    }

    public void update(ProjectType obj) throws SQLException {

        var updateStatement = dbConnection.prepareStatement(
                "UPDATE project_types SET name=?  where id = ?");
        updateStatement.setString(1, obj.getName());
        updateStatement.setInt(2, obj.getId());
        updateStatement.executeUpdate();
    }

    public void delete(int id) throws SQLException {

        var deleteStatement = dbConnection.prepareStatement(
                "DELETE from project_types where id=?");
        deleteStatement.setInt(1, id);
        deleteStatement.executeUpdate();
    }

    public ProjectType getById(int id) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT project_types.id, project_types.name FROM project_types where id = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setInt(1, id);
        statement.executeQuery();
        return convertResultSetToSingleObj(statement.getResultSet());
    }


    public List<ProjectType> getAll() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT project_types.id, project_types.name FROM project_types;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.executeQuery();
        return convertResultSetToList(statement.getResultSet());
    }

}
