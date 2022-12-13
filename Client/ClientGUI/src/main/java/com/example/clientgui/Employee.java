package com.example.clientgui;

import connectionModule.ConnectionModule;
import entities.Position;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class Employee {
    @FXML
    private TableView<entities.Employee> projectsTable;
    @FXML
    private TableColumn<entities.Employee, String> colName;
    public TableColumn<entities.Employee, String> colPosition;
    public TableColumn<entities.Employee, String> colSalary;
    public TableColumn<entities.Employee, String> colFilial;
    public TableColumn<entities.Employee, String> colDate;

    public TableColumn<entities.Employee, String>  colExp;
    @FXML
    public void initialize(){

        colName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<entities.Employee, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<entities.Employee, String> financedProjectStringCellDataFeatures) {
                return new ObservableValueBase<String>() {
                    @Override
                    public String getValue() {
                        return financedProjectStringCellDataFeatures.getValue().getRequest().getUser().getFullName();
                    }
                };
            }
        });
        colPosition.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<entities.Employee, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<entities.Employee, String> employeeStringCellDataFeatures) {
                return new ObservableValueBase<String>() {
                    @Override
                    public String getValue() {
                        return employeeStringCellDataFeatures.getValue().getRequest().getPosition().getName();
                    }
                };
            }
        });
        colSalary.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<entities.Employee, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<entities.Employee, String> financedProjectStringCellDataFeatures) {
                return new ObservableValueBase<String>() {
                    @Override
                    public String getValue() {
                        return String.valueOf(financedProjectStringCellDataFeatures.getValue().getSalary()) + "руб.";
                    }
                };
            }
        });
        colFilial.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<entities.Employee, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<entities.Employee, String> employeeStringCellDataFeatures) {
                return new ObservableValueBase<String>() {
                    @Override
                    public String getValue() {
                        return employeeStringCellDataFeatures.getValue().getFilial().getName();
                    }
                };
            }
        });
        colDate.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<entities.Employee, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<entities.Employee, String> financedProjectStringCellDataFeatures) {
                return new ObservableValueBase<String>() {
                    @Override
                    public String getValue() {
                        DateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
                        return fmt.format(financedProjectStringCellDataFeatures.getValue().getDateOfAcceptance());
                    }
                };
            }
        });
        colExp.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<entities.Employee, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<entities.Employee, String> employeeStringCellDataFeatures) {
                return new ObservableValueBase<String>() {
                    @Override
                    public String getValue() {
                        return employeeStringCellDataFeatures.getValue().getRequest().getUser().getExperience() + "лет.";
                    }
                };
            }
        });

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
        typeCombo.setCellFactory(cellFactory);
        typeCombo.setConverter(new StringConverter<Position>() {
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
        List<Position> list = null;
        try {
            list = ConnectionModule.getAllPositions();
            list.add(new Position(0, "---"));
            ObservableList<Position> projects = FXCollections.observableList(list);
            typeCombo.setItems(projects);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        updatePage();
    }

    private void updatePage(){

        ObservableList<entities.Employee> projects = FXCollections.observableArrayList();

        try {
            var list = ConnectionModule.getAllEmployee();
            for (var item: list) {
                boolean isNeedToShow = true;

                if(creator != null && !creator.isEmpty())
                    isNeedToShow &=item.getRequest().getUser().getFullName().contains(creator);

                if(type != null && !type.getName().equals("---"))
                    isNeedToShow &= item.getRequest().getPosition().equals(type);

                if(costFrom != -1)
                    isNeedToShow &= item.getSalary() >= costFrom;

                if(costTo != -1)
                    isNeedToShow &= item.getSalary() <= costTo;

                if(risqFrom != -1)
                    isNeedToShow &= item.getRequest().getUser().getExperience()>= risqFrom;

                if(risqTo != -1)
                    isNeedToShow &= item.getRequest().getUser().getExperience() <= risqTo;

                if(isNeedToShow)
                    projects.add(item);
            }

            projectsTable.setItems(projects);

        } catch (Exception e) {
            AlertManager.showErrorAlert("Ошибка", "Ошибка соединения");
        }
    }
    @FXML
    private TextField costFromInput;

    @FXML
    private TextField costToInput;

    @FXML
    private TextField creatorSearchInput;


    @FXML
    private TextField riskFromInput;

    @FXML
    private TextField riskToInput;

    @FXML
    private ComboBox<Position> typeCombo;
    private String creator;
    private Position type;

    private double costFrom = -1;
    private double costTo = -1;
    private double risqFrom = -1;
    private double risqTo = -1;
    @FXML
    void onApply(ActionEvent event) {
        String  scostFrom = costFromInput.getText();
        String  scostTo = costToInput.getText();
        String  srisqFrom = riskFromInput.getText();
        String  srisqTo = riskToInput.getText();
          creator = creatorSearchInput.getText();
          type = typeCombo.getValue();

          costFrom = -1;
          costTo = -1;
          risqFrom = -1;
          risqTo = -1;

        if(!scostFrom.isEmpty()){
            try {
                costFrom = Double.parseDouble(scostFrom);
            }
            catch (NumberFormatException e){
                AlertManager.showErrorAlert("Ошибка!", "Поле фильтрации должно содержать только цифры");
                return;
            }
        }

        if(!scostTo.isEmpty()){
            try {
                costTo = Double.parseDouble(scostTo);
            }
            catch (NumberFormatException e){
                AlertManager.showErrorAlert("Ошибка!", "Поле фильтрации должно содержать только цифры");
                return;
            }
        }

        if(!srisqFrom.isEmpty()){
            try {
                risqFrom = Double.parseDouble(srisqFrom);
            }
            catch (NumberFormatException e){
                AlertManager.showErrorAlert("Ошибка!", "Поле фильтрации должно содержать только цифры");
                return;
            }
        }

        if(!srisqTo.isEmpty()){
            try {
                risqTo = Double.parseDouble(srisqTo);
            }
            catch (NumberFormatException e){
                AlertManager.showErrorAlert("Ошибка!", "Поле фильтрации должно содержать только цифры");
                return;
            }
        }

        updatePage();
    }

    @FXML
    void onGoBack(ActionEvent event) {
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.positions, Client.changingWindowUtility.positionsW, Client.changingWindowUtility.positionsH, "Должности");
    }

    @FXML
    void onReport(ActionEvent event) {
        try {
            var list = ConnectionModule.getAllEmployee();

            FileOutputStream out = new FileOutputStream("Report.txt");
            int i=0;
            DateFormat fmt = new SimpleDateFormat("mm:hh dd.MM.yyyy");
            for (var item : list){
                String text = "";
                text +=  ++i +". ФИО: " + item.getRequest().getUser().getFullName() + "\n";
                text += "Должность: " + item.getRequest().getPosition().getName() + "\n";
                text += "З/П: " + item.getSalary() + "руб.\n";
                text += "Стаж:" + fmt.format(item.getDateOfAcceptance()) + "\n";
                text += "Филиал: " + item.getFilial().getName() + "\n";
                text += "Дата найма:" + fmt.format(item.getRequest().getDateOfIssue()) + "\n\n\n";
                out.write(text.getBytes());
            }
            out.close();
            AlertManager.showInformationAlert("Успешно!", "Информация сохранена в файл в папке с программой");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void onSendEmail(ActionEvent event) {
        try {
            List<entities.Employee> list = null;
            try {
                list = ConnectionModule.getAllEmployee();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            String text = "";
            DateFormat fmt = new SimpleDateFormat("mm:hh dd.MM.yyyy");
            int i=0;
            for (var item : list){
                text +=  ++i +". ФИО: " + item.getRequest().getUser().getFullName() + "\n";
                text += "Должность: " + item.getRequest().getPosition().getName() + "\n";
                text += "З/П: " + item.getSalary() + "руб.\n";
                text += "Стаж:" + fmt.format(item.getDateOfAcceptance()) + "\n";
                text += "Филиал: " + item.getFilial().getName() + "\n";
                text += "Дата найма:" + fmt.format(item.getRequest().getDateOfIssue()) + "\n\n\n";
            }


            MailSender sender = new MailSender();
            sender.send("Текущий состав компании", text, "genyalepel9@yandex.ru", null);
            AlertManager.showInformationAlert("Успешно!", "Данные отправлены");

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void onTop(ActionEvent event) {
        try {
            var list = ConnectionModule.getAllEmployee();
            var filials = ConnectionModule.getAllFilials();

            FileOutputStream out = new FileOutputStream("Filials.txt");


            out.write("Список сотрудников по филиалам:".getBytes());
            for(var filial : filials){
                int i=0;
                DateFormat fmt = new SimpleDateFormat("mm:hh dd.MM.yyyy");

                out.write(("\n\n\n\n\nФилиал(" + filial.getName() + "):\n").getBytes());

                for (var item : list){
                    if(item.getFilial().getId() == filial.getId()){
                        String text = "";
                        text +=  ++i +". ФИО: " + item.getRequest().getUser().getFullName() + "\n";
                        text += "Должность: " + item.getRequest().getPosition().getName() + "\n";
                        text += "З/П: " + item.getSalary() + "руб.\n";
                        text += "Стаж:" + fmt.format(item.getDateOfAcceptance()) + "\n";
                        text += "Дата найма:" + fmt.format(item.getRequest().getDateOfIssue()) + "\n";
                        out.write(text.getBytes());
                    }
                }
                if(i==0)
                    out.write("Нет сотрудников\n".getBytes());
            }

            out.close();
            AlertManager.showInformationAlert("Успешно!", "Данные сохранены в файл 'Filials.txt'");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onFire(ActionEvent event) {
        entities.Employee e = projectsTable.getSelectionModel().getSelectedItem();
        if(e == null)
            return;

        try {
            ConnectionModule.deleteEmployee(e.getRequest().getId());
            ConnectionModule.deleteRequest(e.getRequest().getId());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        updatePage();
    }
}
