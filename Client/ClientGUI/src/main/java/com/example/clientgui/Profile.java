package com.example.clientgui;

import connectionModule.ConnectionModule;
import enums.UserType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class Profile {

    @FXML
    public void initialize(){
        fullnameInput.setText(Client.connectedUser.getFullName());
        inputExp.setText(Integer.toString(Client.connectedUser.getExperience()));
    }
    @FXML
    private TextField fullnameInput;

    @FXML
    private TextField inputExp;

    @FXML
    void onCancel(ActionEvent event) {
        if(Client.userType == UserType.ADMIN)
            Client.changingWindowUtility.showWindow(Client.changingWindowUtility.userView, Client.changingWindowUtility.userW, Client.changingWindowUtility.userH, "Управление пользователями");
        else
            Client.changingWindowUtility.showWindow(Client.changingWindowUtility.positions, Client.changingWindowUtility.positionsW, Client.changingWindowUtility.positionsH, "Должности");
    }

    @FXML
    void onOK(ActionEvent event) {
        String fullname = fullnameInput.getText();
        String exp = inputExp.getText();
        if(fullname.isEmpty() || exp.isEmpty()){
            AlertManager.showErrorAlert("Ошибка!", "Введите все поля");
            return;
        }

        int experience = -1;
        try{
            experience = Integer.parseInt(exp);
            if(experience < 0 || experience > 50){
                throw  new NumberFormatException();
            }
        }
        catch (NumberFormatException e){
            AlertManager.showErrorAlert("Ошибка!", "Введите корректный стаж");
            return;
        }

        try {
            Client.connectedUser.setFullName(fullname);
            Client.connectedUser.setExperience(experience);
            ConnectionModule.editUser(Client.connectedUser);
            if(Client.userType == UserType.ADMIN)
                Client.changingWindowUtility.showWindow(Client.changingWindowUtility.userView, Client.changingWindowUtility.userW, Client.changingWindowUtility.userH, "Управление пользователями");
            else
                Client.changingWindowUtility.showWindow(Client.changingWindowUtility.positions, Client.changingWindowUtility.positionsW, Client.changingWindowUtility.positionsH, "Должности");
        } catch (Exception e) {
            AlertManager.showErrorAlert("Ошибка!", "");
        }
    }

}
