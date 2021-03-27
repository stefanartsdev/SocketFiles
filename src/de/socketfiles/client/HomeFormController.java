package de.socketfiles.client;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;

public class HomeFormController implements Initializable {

    private static ListView<String> userListStatic;
    private static TableView<FileMeta> fileListStatic;
    private static String[] lastUsers;
    private static ArrayList<FileMeta> lastFiles;

    @FXML
    private MenuItem uploadFileBtn;

    @FXML
    private MenuItem disconnectBtn;

    @FXML
    private MenuItem helpBtn;

    @FXML
    private ListView<String> userList;

    @FXML
    private Label pathLabel;

    @FXML
    private TableView<FileMeta> fileList;

    @FXML
    void about(ActionEvent event) {

    }

    @FXML
    void disconnect(ActionEvent event) {

    }

    @FXML
    void help(ActionEvent event) {

    }

    @FXML
    void onDragDropped(DragEvent event) {

    }

    @FXML
    void uploadFile(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Datei zum Hochladen auswählen...");
        File upload = fc.showOpenDialog(userList.getScene().getWindow());
        if(upload != null) {
            if(ClientLogic.uploadFile(upload)) {
                showInfo("Die Datei wurde erfolgreich heruntergeladen!", "Dateiupload erfolgreich!", Alert.AlertType.CONFIRMATION);
            } else {
                showInfo("Fehler während des Uploadvorgangs!", "Dateiupload fehlgeschlagen!", Alert.AlertType.ERROR);
            }
        } else {
            showInfo("Du musst eine valide Datei auswählen!", "Dateiupload fehlgeschlagen!", Alert.AlertType.ERROR);
        }
    }

    public static void updateUsers(String[] clients) {
        assert clients != null;
        if(userListStatic == null) {
            lastUsers = clients;
        } else {
            Platform.runLater(() -> {
                userListStatic.getItems().clear();
                for(int i = 0; i < clients.length; i++) {
                    userListStatic.getItems().add(clients[i]);
                }
            });
        }

    }

    public static void updateFiles(ArrayList<FileMeta> files) {
        System.out.println("updating");
        System.out.println(files);
        if(fileListStatic == null) {
            lastFiles = files;
        } else {
            Platform.runLater(() -> {
                fileListStatic.getItems().clear();
                for(FileMeta fm : files) {

                    fileListStatic.getItems().add(new FileMeta(fm.getName(), fm.getAuthor(), fm.getSize() / 1000F));
                }
            });
        }
    }

    private static void showInfo(String msg, String title, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("SFP Client");
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userListStatic = userList;
        fileListStatic = fileList;

        TableColumn<FileMeta, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<FileMeta, String> authorCol = new TableColumn<>("Autor");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        TableColumn<FileMeta, Double> sizeCol = new TableColumn<>("Dateigröße (KB)");
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));
        fileList.getColumns().addAll(nameCol, authorCol, sizeCol);
        updateUsers(lastUsers);
        updateFiles(lastFiles);
    }
}
