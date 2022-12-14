package dbLayer.repositories;

import entities.Filial;
import entities.Position;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FilialsRepository {
    private final Connection dbConnection;

    public FilialsRepository(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    protected static Filial convertResultSetToSingleObj(ResultSet resultSet) throws SQLException {

        resultSet.beforeFirst();
        if (!resultSet.next()) return new Filial();
        return convertResultSetToObj(resultSet);
    }

    private static Filial convertResultSetToObj(ResultSet resultSet) throws SQLException {

        var obj = new Filial();
        obj.setId(resultSet.getInt("filials.id"));
        obj.setName(resultSet.getString("filials.city"));
        return obj;
    }

    protected static List<Filial> convertResultSetToList(ResultSet resultSet) throws SQLException {

        var list = new ArrayList<Filial>();
        resultSet.beforeFirst();
        while (resultSet.next()) {

            list.add(convertResultSetToObj(resultSet));
        }
        return list;
    }

    private int getMaxId() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT MAX(id) from filials;");
        var resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public int create(Filial obj) throws SQLException {

        var insertStatement = dbConnection.prepareStatement(
                "INSERT INTO filials (city) " +
                        "values (?)");

        insertStatement.setString(1, obj.getName());
        insertStatement.executeUpdate();
        return getMaxId();
    }

    public void update(Filial obj) throws SQLException {

        var updateStatement = dbConnection.prepareStatement(
                "UPDATE filials SET city=?  where id = ?");
        updateStatement.setString(1, obj.getName());
        updateStatement.setInt(2, obj.getId());
        updateStatement.executeUpdate();
    }

    public void delete(int id) throws SQLException {

        var deleteStatement = dbConnection.prepareStatement(
                "DELETE from filials where id=?");
        deleteStatement.setInt(1, id);
        deleteStatement.executeUpdate();
    }

    public Filial getById(int id) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT filials.id, filials.city FROM filials where id = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setInt(1, id);
        statement.executeQuery();
        return convertResultSetToSingleObj(statement.getResultSet());
    }


    public List<Filial> getAll() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT filials.id, filials.city FROM filials;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.executeQuery();
        return convertResultSetToList(statement.getResultSet());
    }
}
