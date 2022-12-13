package com.example.clientgui;

import connectionModule.ConnectionModule;
import entities.Position;
import enums.UserType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class Positions {

    public Button employeeButton;
    private UserType userType;

    private ObservableList<Position> listEntities = FXCollections.observableArrayList();
    
    @FXML
    public void initialize(){
        userType = Client.userType;

        if(userType == UserType.USER){
            usersButton.setText("Профиль");
            employeeButton.setDisable(true);

            try {
                var list = ConnectionModule.getAllEmployee();
                for(var item: list){
                    if(item.getRequest().getUser().getId() == Client.connectedUser.getId()){
                        AlertManager.showInformationAlert("Подравляем!", "Вы были приняты на должность " + item.getRequest().getPosition().getName() + " с зарплатой " + item.getSalary() + " руб. в г." + item.getFilial().getName() + ". Больше Вам не нужно отслеживать вакансии!");
                        employeeButton.setDisable(true);
                        requestsButton.setDisable(true);
                        usersButton.setDisable(true);
                        break;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        try {
            var list = ConnectionModule.getAllPositions();
            for (var item: list) {
                listEntities.add(item);
            }
        } catch (Exception e) {
            AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
        }

        typeColumn.setCellValueFactory(new PropertyValueFactory<Position, String>("name"));
        typesTable.setItems(listEntities);
    }
    @FXML
    private TableView<Position> typesTable;
    @FXML
    private TableColumn<Position, String> typeColumn;
    @FXML
    private Button requestsButton;
    @FXML
    private Button usersButton;

    @FXML
    void onAbout(ActionEvent event) {
        String textInfo = "";
        switch (userType){
            case ADMIN -> {
                textInfo = "Вы администратор. Вы можете управлять пользователями, рассматривать их заявки, нанимать сотрудников, увольнять их.";
            }
            case USER -> {
                textInfo = "Вы пользователь. Вы можете составлять заявки на работу.";
            }
        }
        AlertManager.showInformationAlert("Информация о проекте", textInfo);
    }

    @FXML
    void onExit(ActionEvent event) {
        try {
            ConnectionModule.exit();
            Client.userType = UserType.UNDEFINED;
            Client.changingWindowUtility.showWindow(Client.changingWindowUtility.authorization, Client.changingWindowUtility.authorizationW, Client.changingWindowUtility.authorizationH, "Авторизация");
        } catch (IOException e) {
            AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
        }
    }

    @FXML
    void onRequestsButton(ActionEvent event) {
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.requestView, Client.changingWindowUtility.requestW, Client.changingWindowUtility.requestH, "Заявки");
    }

    @FXML
    void onUsersManagement(ActionEvent event) {
        if(userType == UserType.ADMIN)
            Client.changingWindowUtility.showWindow(Client.changingWindowUtility.userView, Client.changingWindowUtility.userW, Client.changingWindowUtility.userH, "Управление пользователями");
        else
            Client.changingWindowUtility.showWindow(Client.changingWindowUtility.profile, Client.changingWindowUtility.profileW, Client.changingWindowUtility.profileH, "Профиль");
    }

    public void onEmployee(ActionEvent event) {
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.employee, Client.changingWindowUtility.employeeW, Client.changingWindowUtility.employeeH, "Сотрудники");
    }
}
