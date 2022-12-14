package serverEndPoint.threads;

import Commands.AuthorizationCommand;
import Commands.Command;
import Commands.Response;
import dbLayer.managers.DataAccessManager;
import entities.*;
import enums.UserType;
import serverEndPoint.ConnectedClientInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

//В этом потоке происходит взаимодействие с клиентом
public class ClientProcessingThread extends Thread {

    private final DataAccessManager dataAccessManager;

    private final ConnectedClientInfo clientInfo;

    private final ObjectOutputStream objectOutputStream;

    private final ObjectInputStream objectInputStream;

    public ClientProcessingThread(ConnectedClientInfo clientInfo, Connection dbConnection) throws IOException {
        this.clientInfo = clientInfo;
        var socket = clientInfo.getConnectionSocket();
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        dataAccessManager = new DataAccessManager(dbConnection);
    }

    private void sendObject(Serializable object) throws IOException {

        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
    }

    private <T> T receiveObject() throws IOException, ClassNotFoundException {

        return (T) objectInputStream.readObject();
    }

    @Override
    public void run() {

        while (true) {
            try {
                switch (clientLobby()) {
                    case ADMIN, USER -> {
                        processing();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void interrupt() {
        try {
            //Заканчиваем работу
            clientInfo.getConnectionSocket().close();
        } catch (IOException e) { //Аналогично
            throw new RuntimeException(e);
        }
        super.interrupt();
    }

    public ConnectedClientInfo getClientInfo() {
        return clientInfo;
    }

    private UserType clientLobby() throws Exception {

        while (true) {

            AuthorizationCommand command = receiveObject();
            switch (command) {
                case AUTHORIZE -> {

                    String login = receiveObject();
                    String password = receiveObject();
                    var user = dataAccessManager.usersRepository.get(login, password);
                    if (user.getId() != 0 && user.getStatus() == UserStatus.NOT_BANNED) {
                        sendObject(UserType.USER);
                        clientInfo.setIdInDB(user.getId());
                        return UserType.USER;
                    }
                    var admin = dataAccessManager.adminsRepository.get(login, password);
                    if (admin.getId() != 0) {
                        sendObject(UserType.ADMIN);
                        clientInfo.setIdInDB(admin.getId());
                        return UserType.ADMIN;
                    }
                    clientInfo.setIdInDB(0);
                    sendObject(UserType.UNDEFINED);
                }
                case CHECK_IF_LOGIN_EXISTS -> {

                    String login = receiveObject();
                    var user = dataAccessManager.usersRepository.get(login);
                    var admin = dataAccessManager.adminsRepository.get(login);
                    if (user.getId() == 0 && admin.getId() == 0) {
                        sendObject(Response.NOT_FOUND);
                    } else {
                        sendObject(Response.SUCCESSFULLY);
                    }
                }
                case REGISTER -> {

                    String login = receiveObject();
                    String password = receiveObject();
                    String fullName = receiveObject();
                    int exp = receiveObject();
                    try {
                        int id = dataAccessManager.usersRepository.create(
                                new User(0, login, password, UserStatus.NOT_BANNED, fullName, exp));
                        clientInfo.setIdInDB(id);
                        sendObject(Response.SUCCESSFULLY);
                        return UserType.USER;
                    } catch (Exception e) {
                        sendObject(Response.ERROR);
                    }
                }
                default -> {
                    sendObject(Response.UNKNOWN_COMMAND);
                }
            }
        }
    }

    private void processing() throws IOException, ClassNotFoundException {

        while (true) {

            Command command = receiveObject();
            switch (command) {

                case EXIT -> {
                    return;
                }
                case GET_ALL_USERS -> {
                    try {
                        var users = dataAccessManager.usersRepository.getAll();
                        sendObject(new ArrayList<>(users));
                    } catch (SQLException e) {
                        sendObject(new ArrayList<>());
                    }
                }
                case BAN_USER -> {

                    int id = receiveObject();
                    try {
                        var user = dataAccessManager.usersRepository.getById(id);
                        user.setStatus(UserStatus.BANNED);
                        dataAccessManager.usersRepository.update(user);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case UNBAN_USER -> {
                    int id = receiveObject();
                    try {
                        var user = dataAccessManager.usersRepository.getById(id);
                        user.setStatus(UserStatus.NOT_BANNED);
                        dataAccessManager.usersRepository.update(user);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case EDIT_USER -> {
                    User user = receiveObject();
                    try {
                        dataAccessManager.usersRepository.update(user);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case GET_ALL_REQUESTS -> {
                    try {
                        var projects = dataAccessManager.requestsRepository.getAll();
                        sendObject(new ArrayList<>(projects));
                    } catch (SQLException e) {
                        sendObject(new ArrayList<>());
                    }
                }
                case GET_ALL_EMPLOYEE -> {
                    try {
                        var projects = dataAccessManager.employeeRepository.getAll();
                        sendObject(new ArrayList<>(projects));
                    } catch (SQLException e) {
                        sendObject(new ArrayList<>());
                    }
                }
                case GET_ALL_POSITIONS -> {
                    try {
                        var projectTypes = dataAccessManager.positionsRepository.getAll();
                        sendObject(new ArrayList<>(projectTypes));
                    } catch (SQLException e) {
                        sendObject(new ArrayList<>());
                    }
                }
                case GET_ALL_FILIALS -> {
                    try {
                        var projectTypes = dataAccessManager.filialsRepository.getAll();
                        sendObject(new ArrayList<>(projectTypes));
                    } catch (SQLException e) {
                        sendObject(new ArrayList<>());
                    }
                }
                case CREATE_REQUEST -> {
                    Request request = receiveObject();
                    try {
                        dataAccessManager.requestsRepository.create(request);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case APPLY_REQUEST -> {
                    Request request = receiveObject();
                    Filial filial = receiveObject();
                    float salary = receiveObject();
                    try {
                        dataAccessManager.employeeRepository.create(new Employee(request, new Date(), filial, salary));
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case UPDATE_REQUEST -> {
                    Request request = receiveObject();
                    try {
                        dataAccessManager.requestsRepository.update(request);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case DELETE_REQUEST -> {
                    int id = receiveObject();
                    try {
                        dataAccessManager.requestsRepository.delete(id);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case DELETE_EMPLOYEE -> {
                    int id = receiveObject();
                    try {
                        dataAccessManager.employeeRepository.delete(id);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
            }
        }
    }
}