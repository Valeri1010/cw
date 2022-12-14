package dbLayer.repositories;

import dbLayer.managers.DataAccessManager;
import entities.Employee;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {
    private final Connection dbConnection;

    public EmployeeRepository(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    protected Employee convertResultSetToSingleObj(ResultSet resultSet) throws SQLException {

        resultSet.beforeFirst();
        if (!resultSet.next()) return new Employee();
        return convertResultSetToObj(resultSet);
    }

    private Employee convertResultSetToObj(ResultSet resultSet) throws SQLException {

        var obj = new Employee();
        obj.setRequest(new DataAccessManager(dbConnection).requestsRepository.getById(resultSet.getInt("requestId")));
        obj.setFilial(new DataAccessManager(dbConnection).filialsRepository.getById(resultSet.getInt("filialId")));
        obj.setDateOfAcceptance(resultSet.getTimestamp("dateOfHiring"));
        obj.setSalary(resultSet.getFloat("salary"));
        return obj;
    }

    protected List<Employee> convertResultSetToList(ResultSet resultSet) throws SQLException {

        var list = new ArrayList<Employee>();
        resultSet.beforeFirst();
        while (resultSet.next()) {

            list.add(convertResultSetToObj(resultSet));
        }
        return list;
    }

    public void create(Employee obj) throws SQLException {

        var insertStatement = dbConnection.prepareStatement(
                "INSERT INTO employee (requestId, filialId, dateOfHiring, salary) " +
                        " VALUES (?, ?, ?, ?)");

        insertStatement.setInt(1, obj.getRequest().getId());
        insertStatement.setInt(2, obj.getFilial().getId());
        insertStatement.setTimestamp(3, new Timestamp(obj.getDateOfAcceptance().getTime()));
        insertStatement.setFloat(4, obj.getSalary());
        insertStatement.executeUpdate();
    }

    public void delete(int projectId) throws SQLException {

        var deleteStatement = dbConnection.prepareStatement(
                "DELETE from employee where requestId=?");
        deleteStatement.setInt(1, projectId);
        deleteStatement.executeUpdate();
    }

    public Employee getById(int projectId) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM employee where requestId = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setInt(1, projectId);
        statement.executeQuery();
        return convertResultSetToSingleObj(statement.getResultSet());
    }

    public List<Employee> getAll() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM employee;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.executeQuery();
        return convertResultSetToList(statement.getResultSet());
    }
}