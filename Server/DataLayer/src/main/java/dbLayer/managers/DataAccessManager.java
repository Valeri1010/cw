package dbLayer.managers;

import dbLayer.repositories.*;

import java.sql.Connection;

public class DataAccessManager {

    public final UsersRepository usersRepository;

    public final AdminsRepository adminsRepository;

    public final EmployeeRepository employeeRepository;

    public final RequestsRepository requestsRepository;

    public final PositionsRepository positionsRepository;
    public final FilialsRepository filialsRepository;


    public DataAccessManager(Connection connection) {

        usersRepository = new UsersRepository(connection);
        adminsRepository = new AdminsRepository(connection);
        employeeRepository = new EmployeeRepository(connection);
        requestsRepository = new RequestsRepository(connection);
        positionsRepository = new PositionsRepository(connection);
        filialsRepository = new FilialsRepository(connection);
    }
}
