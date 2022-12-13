package com.example.clientgui;

import connectionModule.ConnectionModule;
import entities.User;
import entities.UserStatus;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class Users {

    @FXML
    public void initialize(){
        columnFullName.setCellValueFactory(new PropertyValueFactory<User, String>("fullName"));
        columnExp.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<User, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<User, String> userStringCellDataFeatures) {
                return new ObservableValueBase<String>() {
                    @Override
                    public String getValue() {
                        return userStringCellDataFeatures.getValue().getExperience() + "лет";
                    }
                };
            }
        });
        columnStatus.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<User, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<User, String> userStringCellDataFeatures) {
                return new ObservableValueBase<String>() {
                    @Override
                    public String getValue() {
                        return userStringCellDataFeatures.getValue().getStatus() == UserStatus.BANNED? "Заблокирован": "Активен";
                    }
                };
            }
        });

        updatePage();
    }

    @FXML
    public void updatePage() {
        ObservableList<User> users = FXCollections.observableArrayList();

        try {
            var list = ConnectionModule.getAllUsers();
            for (var item: list) {
                users.add(item);
            }
        } catch (Exception e) {
            AlertManager.showErrorAlert("Ошибка", "Ошибка соединения");
        }
        usersTable.setItems(users);
    }

    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, String> columnFullName;

    @FXML
    private TableColumn<User, String> columnExp;

    @FXML
    private TableColumn<User, String> columnStatus;

    @FXML
    void onBan(ActionEvent event) {
        var user = usersTable.getSelectionModel().getSelectedItem();
        if(user != null){
            user.setStatus(UserStatus.BANNED);
            try {
                ConnectionModule.banUser(user.getId());
            } catch (Exception e) {
                AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
            }
        }
        updatePage();
    }

    @FXML
    void onEdit(ActionEvent event) {
        var user = usersTable.getSelectionModel().getSelectedItem();
        if(user == null)
            return;

        Client.connectedUser = user;

        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.profile, Client.changingWindowUtility.profileW, Client.changingWindowUtility.profileH, "Редактирование профиля");
    }

    @FXML
    void onGoBack(ActionEvent event) {
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.positions, Client.changingWindowUtility.positionsW, Client.changingWindowUtility.positionsH, "Должности");
    }

    @FXML
    void onUnban(ActionEvent event) {
        var user = usersTable.getSelectionModel().getSelectedItem();
        if(user != null){
            user.setStatus(UserStatus.NOT_BANNED);
            try {
                ConnectionModule.unbanUser(user.getId());
            } catch (Exception e) {
                AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
            }
        }
        updatePage();
    }

}
