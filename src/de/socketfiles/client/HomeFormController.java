package de.socketfiles.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
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
        if(ClientLogic.disconnect()) {
            Platform.exit();
        }
    }

    @FXML
    void help(ActionEvent event) {

    }

    @FXML
    void onDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    @FXML
    void onDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        if(db.hasFiles()) {
            List<File> filesToUpload = db.getFiles();
            if(filesToUpload.size() != 1) {
                showInfo("Du kannst nur maximal eine Datei auf einmal hochladen!", "Dateiupload fehlgeschlagen!", Alert.AlertType.ERROR);
                return;
            } else if(filesToUpload.get(0) == null) {
                showInfo("Deine Datei ist ungültig!", "Dateiupload fehlgeschlagen!", Alert.AlertType.ERROR);
                return;
            }
            event.setDropCompleted(true);
            if(ClientLogic.uploadFile(filesToUpload.get(0))) {
                showInfo("Die Datei wurde erfolgreich heruntergeladen!", "Dateiupload erfolgreich!", Alert.AlertType.CONFIRMATION);
            } else {
                showInfo("Fehler während des Uploadvorgangs!", "Dateiupload fehlgeschlagen!", Alert.AlertType.ERROR);
            }
        }
        event.consume();

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

    public static void saveFile(String name, byte[] data) {
        Platform.runLater(() -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Datei " + name + " speichern unter...");
            fc.setInitialFileName(name);
            File save = fc.showSaveDialog(userListStatic.getScene().getWindow());
                try {
                    Files.write(Paths.get(save.getPath()), data);
                } catch (IOException e) {
                    showInfo("Die Datei konnte nicht gespeichert werden!", "Speichern fehlgeschlagen!", Alert.AlertType.ERROR);
                    System.out.println("Could not save file:");
                    e.printStackTrace();
                }
        });
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

        fileList.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
                ClientLogic.requestDownload(fileList.getSelectionModel().getSelectedItem());
            }
        });
        updateUsers(lastUsers);
        updateFiles(lastFiles);
    }
}
