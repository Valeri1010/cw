package com.example.clientgui;

import connectionModule.ConnectionModule;
import entities.Request;
import enums.UserType;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Requests {

    public ComboBox<String> comboPosition;
    public Button editBtn;

    @FXML
    private TableView<Request> requestTable;

    @FXML
    private TableColumn<Request, String> colDate;
    @FXML
    public TableColumn<Request, String> colFullname;
    @FXML
    public TableColumn<Request, String> colExp;
    @FXML
    public TableColumn<Request, String> colPosition;

    @FXML
    public void initialize(){
        discardButton.setVisible(Client.userType == UserType.ADMIN);
        editBtn.setVisible(Client.userType == UserType.ADMIN);
        if(Client.userType == UserType.USER){
            applyButton.setText("Заявка");
        }

        colFullname.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Request, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Request, String> financedProjectStringCellDataFeatures) {
                return new ObservableValueBase<String>() {
                    @Override
                    public String getValue() {
                        return financedProjectStringCellDataFeatures.getValue().getUser().getFullName();
                    }
                };
            }
        });
        colPosition.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Request, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Request, String> financedProjectStringCellDataFeatures) {
                return new ObservableValueBase<String>() {
                    @Override
                    public String getValue() {
                        return financedProjectStringCellDataFeatures.getValue().getPosition().getName();
                    }
                };
            }
        });
        colExp.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Request, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Request, String> financedProjectStringCellDataFeatures) {
                return new ObservableValueBase<String>() {
                    @Override
                    public String getValue() {
                        return String.valueOf(financedProjectStringCellDataFeatures.getValue().getUser().getExperience()) + "лет.";
                    }
                };
            }
        });
        colDate.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Request, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Request, String> financedProjectStringCellDataFeatures) {
                return new ObservableValueBase<String>() {
                    @Override
                    public String getValue() {
                        DateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
                        return fmt.format(financedProjectStringCellDataFeatures.getValue().getDateOfIssue());
                    }
                };
            }
        });

        try {
            var list = ConnectionModule.getAllPositions();
            ObservableList<String> positions = FXCollections.observableArrayList();
            for(var item: list){
                positions.add(item.getName());
            }
            positions.add("---");
            comboPosition.setItems(positions);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        updatePage();
    }

    public void updatePage(){
        ObservableList<Request> projects = FXCollections.observableArrayList();

        String searchPosition = comboPosition.getValue();
        if(searchPosition==null||  searchPosition.contains("---"))
            searchPosition = "";

        try {
            var list = ConnectionModule.getAllRequests();
            for (var item: list) {
                boolean isNeedToShow = true;

                if(!searchPosition.isEmpty())
                    isNeedToShow &= item.getPosition().getName().equals(searchPosition);

                if(Client.userType == UserType.USER)
                    isNeedToShow &= Client.connectedUser.equals(item.getUser());

                if(isNeedToShow)
                    projects.add(item);
            }

            requestTable.setItems(projects);

        } catch (Exception e) {
            AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
        }
    }
    @FXML
    private Button applyButton;
    @FXML
    private Button discardButton;

    @FXML
    void onApply(ActionEvent event) {
        Client.request = null;
        if(Client.userType == UserType.ADMIN){
            var item = requestTable.getSelectionModel().getSelectedItem();
            if(item != null){
                try {
                    Client.request = item;
                    Client.changingWindowUtility.showWindow(Client.changingWindowUtility.applyRequest, Client.changingWindowUtility.applyRequestW, Client.changingWindowUtility.applyRequestH, "Взятие на работу");
                } catch (Exception e) {
                    AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
                }
            }
        }
        else{
            Client.changingWindowUtility.showWindow(Client.changingWindowUtility.create, Client.changingWindowUtility.createW, Client.changingWindowUtility.createH, "Создание заявки");
        }
    }

    @FXML
    void onApplyFilter(ActionEvent event) {
        updatePage();
    }

    @FXML
    void onDiscard(ActionEvent event) {
        var item = requestTable.getSelectionModel().getSelectedItem();
        if(item != null){
            try {
                ConnectionModule.deleteRequest(item.getId());
                updatePage();
            } catch (Exception e) {
                AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
            }
        }
    }

    @FXML
    void onGoBack(ActionEvent event) {
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.positions, Client.changingWindowUtility.positionsW, Client.changingWindowUtility.positionsH, "Должности");
    }

    public void onEdit(ActionEvent event) {
        Client.request = null;
        if(Client.userType == UserType.ADMIN){
            var item = requestTable.getSelectionModel().getSelectedItem();
            if(item != null){
                try {
                    Client.request = item;
                    Client.changingWindowUtility.showWindow(Client.changingWindowUtility.create, Client.changingWindowUtility.createW, Client.changingWindowUtility.createH, "Редактирование заявки");
                } catch (Exception e) {
                    AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
                }
            }
        }
    }
}
