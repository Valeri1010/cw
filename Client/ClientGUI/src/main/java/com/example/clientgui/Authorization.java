package com.example.clientgui;

import connectionModule.ConnectionModule;
import enums.UserType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Authorization {

    @FXML
    private TextField loginInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    void onEnter(ActionEvent event) {
        var login = loginInput.getText();
        var password = passwordInput.getText();

        try {

            Client.userType = ConnectionModule.singUp(login, password);
            if(Client.userType == UserType.UNDEFINED){
                AlertManager.showErrorAlert("Пользователь не найден!", "");
            }
            else{
                if(Client.userType == UserType.USER) {
                    var users = ConnectionModule.getAllUsers();
                    for (var user : users) {
                        if (user.getLogin().equals(login)) {
                            Client.connectedUser = user;
                            break;
                        }
                    }
                }
                Client.changingWindowUtility.showWindow(Client.changingWindowUtility.positions, Client.changingWindowUtility.positionsW, Client.changingWindowUtility.positionsH, "Должности");
            }
        } catch (Exception e) {
            AlertManager.showErrorAlert("Ошибка соединения", "");
        }
    }

    @FXML
    void onRegistration(ActionEvent event) {
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.registration, Client.changingWindowUtility.registrationW, Client.changingWindowUtility.registrationH, "Регистрация");
    }

}
