package dbLayer.managers;

import dbLayer.repositories.*;

import java.sql.Connection;

public class DataAccessManager {

    public final UsersRepository usersRepository;

    public final AdminsRepository adminsRepository;

    public final FinancedProjectsRepository financedProjectsRepository;

    public final ProjectsRequestsRepository projectsRequestsRepository;

    public final ProjectTypesRepository projectTypesRepository;


    public DataAccessManager(Connection connection) {

        usersRepository = new UsersRepository(connection);
        adminsRepository = new AdminsRepository(connection);
        financedProjectsRepository = new FinancedProjectsRepository(connection);
        projectsRequestsRepository = new ProjectsRequestsRepository(connection);
        projectTypesRepository = new ProjectTypesRepository(connection);
    }
}
