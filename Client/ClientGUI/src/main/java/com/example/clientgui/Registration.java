package com.example.clientgui;

import Commands.Response;
import connectionModule.ConnectionModule;
import enums.UserType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Registration {

    @FXML
    private TextField fullnameInput;

    @FXML
    private TextField loginInput;

    @FXML
    private TextField expInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private PasswordField repeatPasswordInput;

    @FXML
    void onCancel(ActionEvent event) {
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.authorization, Client.changingWindowUtility.authorizationW, Client.changingWindowUtility.authorizationH, "Авторизация");
    }

    @FXML
    void onEnter(ActionEvent event) {
        var login = loginInput.getText();
        var password = passwordInput.getText();
        var repeatPassword = repeatPasswordInput.getText();
        var fullname = fullnameInput.getText();
        var exp = expInput.getText();

        if(login.isEmpty() ||password.isEmpty() ||repeatPassword.isEmpty() ||exp.isEmpty() ||fullname.isEmpty()){
            AlertManager.showWarningAlert("Поля должны быть заполнены", "Заполните все поля!");
            return;
        }
        if(!password.equals(repeatPassword)){
            AlertManager.showWarningAlert("Ошибка", "Пароли должны совпадать!");
            return;
        }

        int experience = 0;
        try
        {
            experience = Integer.parseInt(exp);
            if(experience < 0 || experience > 50){
                throw  new NumberFormatException();
            }
        }
        catch (NumberFormatException e){
            AlertManager.showErrorAlert ("Ошибка!", "Введите корректный стаж работы (0-50 лет)");
            return;
        }

        try {

            if(ConnectionModule.registration(login, password, fullname, experience) == Response.SUCCESSFULLY){
                Client.userType = UserType.USER;
                var users = ConnectionModule.getAllUsers();
                for (var user: users) {
                    if(user.getLogin().equals(login)){
                        Client.connectedUser = user;
                        break;
                    }
                }
                Client.changingWindowUtility.showWindow(Client.changingWindowUtility.positions, Client.changingWindowUtility.positionsW, Client.changingWindowUtility.positionsH, "Должности");
            }
            else{
                AlertManager.showErrorAlert("Ошибка", "Пользователь с таким логином уже существует");
            }

        } catch (Exception e) {
            AlertManager.showErrorAlert("Ошибка соединения", "");
        }
    }

}
