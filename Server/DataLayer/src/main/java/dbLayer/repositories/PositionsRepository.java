package dbLayer.repositories;

import entities.Position;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PositionsRepository {

    private final Connection dbConnection;

    public PositionsRepository(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    protected static Position convertResultSetToSingleObj(ResultSet resultSet) throws SQLException {

        resultSet.beforeFirst();
        if (!resultSet.next()) return new Position();
        return convertResultSetToObj(resultSet);
    }

    private static Position convertResultSetToObj(ResultSet resultSet) throws SQLException {

        var obj = new Position();
        obj.setId(resultSet.getInt("positions.id"));
        obj.setName(resultSet.getString("positions.name"));
        return obj;
    }

    protected static List<Position> convertResultSetToList(ResultSet resultSet) throws SQLException {

        var list = new ArrayList<Position>();
        resultSet.beforeFirst();
        while (resultSet.next()) {

            list.add(convertResultSetToObj(resultSet));
        }
        return list;
    }

    private int getMaxId() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT MAX(id) from positions;");
        var resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public int create(Position obj) throws SQLException {

        var insertStatement = dbConnection.prepareStatement(
                "INSERT INTO positions (name) " +
                        "values (?)");

        insertStatement.setString(1, obj.getName());
        insertStatement.executeUpdate();
        return getMaxId();
    }

    public void update(Position obj) throws SQLException {

        var updateStatement = dbConnection.prepareStatement(
                "UPDATE positions SET name=?  where id = ?");
        updateStatement.setString(1, obj.getName());
        updateStatement.setInt(2, obj.getId());
        updateStatement.executeUpdate();
    }

    public void delete(int id) throws SQLException {

        var deleteStatement = dbConnection.prepareStatement(
                "DELETE from positions where id=?");
        deleteStatement.setInt(1, id);
        deleteStatement.executeUpdate();
    }

    public Position getById(int id) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT positions.id, positions.name FROM positions where id = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setInt(1, id);
        statement.executeQuery();
        return convertResultSetToSingleObj(statement.getResultSet());
    }


    public List<Position> getAll() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT positions.id, positions.name FROM positions;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.executeQuery();
        return convertResultSetToList(statement.getResultSet());
    }

}
