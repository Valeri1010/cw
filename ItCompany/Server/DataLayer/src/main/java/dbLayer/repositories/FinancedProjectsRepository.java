package dbLayer.repositories;

import dbLayer.managers.DataAccessManager;
import entities.FinancedProject;
import entities.ProjectRequest;
import entities.ProjectType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FinancedProjectsRepository {
    private final Connection dbConnection;

    public FinancedProjectsRepository(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    protected FinancedProject convertResultSetToSingleObj(ResultSet resultSet) throws SQLException {

        resultSet.beforeFirst();
        if (!resultSet.next()) return new FinancedProject();
        return convertResultSetToObj(resultSet);
    }

    private FinancedProject convertResultSetToObj(ResultSet resultSet) throws SQLException {

        var obj = new FinancedProject();
        obj.setProject(new DataAccessManager(dbConnection).projectsRequestsRepository.getById(resultSet.getInt("projectId")));
        obj.setDateOfAcceptance(resultSet.getTimestamp("dateOfFinansing"));
        return obj;
    }

    protected List<FinancedProject> convertResultSetToList(ResultSet resultSet) throws SQLException {

        var list = new ArrayList<FinancedProject>();
        resultSet.beforeFirst();
        while (resultSet.next()) {

            list.add(convertResultSetToObj(resultSet));
        }
        return list;
    }

    public void addFinancedProject(FinancedProject obj) throws SQLException {

        var insertStatement = dbConnection.prepareStatement(
                "INSERT INTO finansed_projects (projectId, dateOfFinansing) " +
                        " VALUES (?, ?)");

        insertStatement.setInt(1, obj.getProject().getId());
        insertStatement.setTimestamp(2, new Timestamp(obj.getDateOfAcceptance().getTime()));
        insertStatement.executeUpdate();
    }

    public void delete(int projectId) throws SQLException {

        var deleteStatement = dbConnection.prepareStatement(
                "DELETE from finansed_projects where projectId=?");
        deleteStatement.setInt(1, projectId);
        deleteStatement.executeUpdate();
    }

    public FinancedProject getById(int projectId) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM finansed_projects where projectId = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setInt(1, projectId);
        statement.executeQuery();
        return convertResultSetToSingleObj(statement.getResultSet());
    }

    public List<FinancedProject> getAll() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM finansed_projects;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.executeQuery();
        return convertResultSetToList(statement.getResultSet());
    }
}