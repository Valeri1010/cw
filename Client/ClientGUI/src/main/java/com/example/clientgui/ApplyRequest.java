package com.example.clientgui;

import connectionModule.ConnectionModule;
import entities.Filial;
import entities.Position;
import entities.Request;
import enums.UserType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.Date;

public class ApplyRequest {
    @FXML
    public void initialize(){

        try {
            var list = ConnectionModule.getAllFilials();

            Callback<ListView<Filial>, ListCell<Filial>> cellFactory = new Callback<ListView<Filial>, ListCell<Filial>>() {
                @Override
                public ListCell<Filial> call(ListView<Filial> l) {
                    return new ListCell<Filial>() {
                        @Override
                        protected void updateItem(Filial item, boolean empty) {
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
            comboFilial.setCellFactory(cellFactory);
            comboFilial.setConverter(new StringConverter<Filial>() {
                @Override
                public String toString(Filial position) {
                    return String.valueOf(position.getId()) + "." + position.getName();
                }

                @Override
                public Filial fromString(String s) {
                    int id = Integer.parseInt(s.substring(0, s.indexOf('.') - 1));
                    String name = s.substring(s.indexOf('.')+1);

                    Filial position = new Filial(id, name);
                    return position;
                }
            });

            ObservableList<Filial> projects = FXCollections.observableList(list);
            comboFilial.setItems(projects);

        } catch (Exception e) {
            AlertManager.showErrorAlert("Ошибка", "");
        }
    }
    public ComboBox<Filial> comboFilial;
    public TextField inputSalary;

    public void onSave(ActionEvent event) {
        try {
                Filial filial = comboFilial.getValue();
                if(filial == null) {
                    AlertManager.showErrorAlert("Ошибка", "Выберите филиал");
                    return;
                }

                String sSalary = inputSalary.getText();
                if(sSalary.isEmpty()){
                    AlertManager.showErrorAlert("Ошибка", "Введите зарплату");
                    return;
                }

                float salary = 0;
                try{
                    salary = Float.parseFloat(sSalary);
                    if(salary <=0)
                        throw new NumberFormatException();
                }
                catch (NumberFormatException e){
                    AlertManager.showErrorAlert("Ошибка", "Введите корретную зарплату");
                    return;
                }
                ConnectionModule.applyRequest(Client.request, filial, salary);
                var requests = ConnectionModule.getAllRequests();
                for(var item : requests){
                    if(item.getUser().getId() == Client.request.getId() && item.getId() != Client.request.getId()){
                        ConnectionModule.deleteRequest(item.getId());
                    }
                }
                Client.changingWindowUtility.showWindow(Client.changingWindowUtility.requestView, Client.changingWindowUtility.requestW, Client.changingWindowUtility.requestH, "Заявки");
        } catch (Exception e) {
            AlertManager.showErrorAlert("Ошибка соединения", "");
            return;
        }
    }

    public void onCancel(ActionEvent event) {
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.requestView, Client.changingWindowUtility.requestW, Client.changingWindowUtility.requestH, "Заявки");
    }
}
