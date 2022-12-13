package connectionModule;

import Commands.AuthorizationCommand;
import Commands.Command;
import Commands.Response;
import entities.*;
import enums.UserType;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Properties;

public class ConnectionModule {

    private static Socket connectionSocket;
    private static final String serverIp;
    private static final int serverPort;
    private static ObjectOutputStream objectOutputStream;
    private static ObjectInputStream objectInputStream;

    private static Properties getPropertiesFromConfig() throws IOException {

        var properties = new Properties();
        String propFileName = "Client/ConnectionModule/src/main/resources/config.properties";
        var inputStream = new FileInputStream(propFileName);
        if (inputStream == null)
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        properties.load(inputStream);
        return properties;
    }

    static {
        try {
            var properties = getPropertiesFromConfig();
            serverIp = properties.getProperty("serverIp");
            serverPort = Integer.parseInt(properties.getProperty("serverPort"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean connectToServer() throws IOException {

        connectionSocket = new Socket(serverIp, serverPort);
        //connectionSocket.setSoTimeout(3000);
        if (!connectionSocket.isConnected()) return false;
        objectOutputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
        objectInputStream = new ObjectInputStream(connectionSocket.getInputStream());
        return true;
    }

    private static void sendObject(Serializable object) throws IOException {

        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
    }

    private static  <T> T receiveObject() throws Exception {

        return (T) objectInputStream.readObject();
    }

    public static UserType singUp(String login, String password) throws Exception {

        sendObject(AuthorizationCommand.AUTHORIZE);
        sendObject(login);
        sendObject(password);
        return receiveObject();
    }

    public static Response registration(String login, String password, String fullName, int exp) throws Exception {

        sendObject(AuthorizationCommand.REGISTER);
        sendObject(login);
        sendObject(password);
        sendObject(fullName);
        sendObject(exp);
        return receiveObject();
    }

    //ТОЛЬКО ПРИ РЕГИСТРАЦИИ
    public static boolean checkIfLoginExists(String login) throws Exception {

        sendObject(AuthorizationCommand.CHECK_IF_LOGIN_EXISTS);
        sendObject(login);
        Response response = receiveObject();
        return response == Response.SUCCESSFULLY;
    }

    public static void exit() throws IOException {
        sendObject(Command.EXIT);
    }

    public static List<User> getAllUsers() throws Exception {
        sendObject(Command.GET_ALL_USERS);
        return receiveObject();
    }

    public static Response banUser(int userId) throws Exception {
        sendObject(Command.BAN_USER);
        sendObject(userId);
        return receiveObject();
    }

    public static Response unbanUser(int userId) throws Exception {
        sendObject(Command.UNBAN_USER);
        sendObject(userId);
        return receiveObject();
    }

    public static Response editUser(User newVersion) throws Exception {
        sendObject(Command.EDIT_USER);
        sendObject(newVersion);
        return receiveObject();
    }

    public static List<Request> getAllRequests() throws Exception {
        sendObject(Command.GET_ALL_REQUESTS);
        return receiveObject();
    }

    public static List<Filial> getAllFilials() throws Exception {
        sendObject(Command.GET_ALL_FILIALS);
        return receiveObject();
    }
    public static List<Employee> getAllEmployee() throws Exception {
        sendObject(Command.GET_ALL_EMPLOYEE);
        return receiveObject();
    }

    public static List<Position> getAllPositions() throws Exception {
        sendObject(Command.GET_ALL_POSITIONS);
        return receiveObject();
    }

    public static Response createRequest(Request request) throws Exception {
        sendObject(Command.CREATE_REQUEST);
        sendObject(request);
        return receiveObject();
    }

    public static Response updateRequest(Request request) throws Exception {
        sendObject(Command.UPDATE_REQUEST);
        sendObject(request);
        return receiveObject();
    }

    public static Response applyRequest(Request request, Filial filial, float salary) throws Exception {
        sendObject(Command.APPLY_REQUEST);
        sendObject(request);
        sendObject(filial);
        sendObject(salary);
        return receiveObject();
    }

    public static Response deleteRequest(int projectRequestId) throws Exception {
        sendObject(Command.DELETE_REQUEST);
        sendObject(projectRequestId);
        return receiveObject();
    }
    public static Response deleteEmployee(int projectRequestId) throws Exception {
        sendObject(Command.DELETE_EMPLOYEE);
        sendObject(projectRequestId);
        return receiveObject();
    }
}