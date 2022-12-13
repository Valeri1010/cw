package com.example.clientgui;

import connectionModule.ConnectionModule;
import entities.Request;
import entities.Position;
import enums.UserType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.Date;

public class CreateRequest {

    public ComboBox<Position> comboPosition;
    public CheckBox checkSoft;
    public CheckBox checkHard;
    public CheckBox checkEnglish;
    public TextArea inputDescription;

    @FXML
    public void initialize(){

        try {
            var list = ConnectionModule.getAllPositions();

            comboPosition.setDisable(Client.userType != UserType.USER);
            checkSoft.setDisable(Client.userType == UserType.USER);
            checkHard.setDisable(Client.userType == UserType.USER);
            checkEnglish.setDisable(Client.userType == UserType.USER);
            inputDescription.setDisable(Client.userType == UserType.USER);

            Callback<ListView<Position>, ListCell<Position>> cellFactory = new Callback<ListView<Position>, ListCell<Position>>() {
                @Override
                public ListCell<Position> call(ListView<Position> l) {
                    return new ListCell<Position>() {
                        @Override
                        protected void updateItem(Position item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setGraphic(null);
                            } else {
                                setText(item.getName());
                            }
                        }
                    } ;
                }
            };
            comboPosition.setCellFactory(cellFactory);
            comboPosition.setConverter(new StringConverter<Position>() {
                @Override
                public String toString(Position position) {
                    return String.valueOf(position.getId()) + "." + position.getName();
                }

                @Override
                public Position fromString(String s) {
                    int id = Integer.parseInt(s.substring(0, s.indexOf('.') - 1));
                    String name = s.substring(s.indexOf('.')+1);

                    Position position = new Position(id, name);
                    return position;
                }
            });

            ObservableList<Position> projects = FXCollections.observableList(list);
            comboPosition.setItems(projects);

            if(Client.request != null){
                comboPosition.setValue(Client.request.getPosition());
                checkSoft.setSelected(Client.request.isPassedSoft());
                checkHard.setSelected(Client.request.isPassedHard());
                checkEnglish.setSelected(Client.request.isPassedEnglish());
                inputDescription.setText(Client.request.getDescription());
            }
        } catch (Exception e) {
            AlertManager.showErrorAlert("Ошибка", "");
        }
    }
    @FXML
    void onCancel(ActionEvent event) {
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.requestView, Client.changingWindowUtility.requestW, Client.changingWindowUtility.requestH, "Заявки");
    }
    @FXML
    void onSave(ActionEvent event) {
        try {
            if(Client.request != null){
                Client.request.setPassedSoft(checkSoft.isSelected());
                Client.request.setPassedHard(checkHard.isSelected());
                Client.request.setPassedEnglish(checkEnglish.isSelected());
                Client.request.setDescription(inputDescription.getText());
                ConnectionModule.updateRequest(Client.request);
            }
            else{
                Position pos = comboPosition.getValue();
                if(pos == null){
                    AlertManager.showErrorAlert("Выберите должность", "");
                    return;
                }

                ConnectionModule.createRequest(new Request(0, pos, Client.connectedUser, false, false, false, "", new Date()));
            }
            Client.changingWindowUtility.showWindow(Client.changingWindowUtility.requestView, Client.changingWindowUtility.requestW, Client.changingWindowUtility.requestH, "Заявки");
        } catch (Exception e) {
            AlertManager.showErrorAlert("Ошибка соединения", "");
            return;
        }
    }

}
