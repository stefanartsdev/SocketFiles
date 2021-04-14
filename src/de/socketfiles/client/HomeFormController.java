package de.socketfiles.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the JavaFX home window
 */
public class HomeFormController implements Initializable {

    private static ListView<String> userListStatic;
    private static TableView<FileMeta> fileListStatic;
    /**
     * Holds last known user names
     */
    private static String[] lastUsers;
    /**
     * Holds last known files with information
     */
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

    /**
     * Action event for the about button
     * Shows an information alert and opens a web page
     * @param event
     */
    @FXML
    void about(ActionEvent event) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI("http://www.stefanarts.net"));
            } catch (IOException|URISyntaxException e) {}
        }
        showInfo("Dieses Programm wurde von Stefan H. geschrieben", "About", Alert.AlertType.INFORMATION);
    }

    /**
     * Action event for the disconnect button
     * Tries to disconnect the client and then terminates the program
     * @param event
     */
    @FXML
    void disconnect(ActionEvent event) {
        if(ClientLogic.disconnect()) {
            Platform.exit();
        }
    }

    /**
     * Actioon event for the help button
     * Shows a help alert
     * @param event
     */
    @FXML
    void help(ActionEvent event) {
        showInfo("Um eine Datei hochzuladen, ziehen Sie sie entweder in das Fenster oder klicken Sie auf" +
                "Aktionen -> Datei hochladen.\n" +
                "Um eine Datei herunterzuladen, doppelklicken Sie auf diese Datei in der Liste.\n" +
                "Um die Verbindung zu trennen, schließen Sie die Anwendung oder klicken Sie auf Aktionen ->" +
                "Verbindung trennen.", "Hilfe", Alert.AlertType.INFORMATION);
    }

    /**
     * Accepts a drag if clipboard contains a file
     * @param event
     */
    @FXML
    void onDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    /**
     * Handles the drag dropped event
     * Validates the clipboard and then tries to upload the dropped file via {@link de.socketfiles.client.ClientLogic#uploadFile(File)}
     * @param event
     */
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

    /**
     * Handles the upload file button
     * Opens a file dialog and tries to upload the selected file via {@link de.socketfiles.client.ClientLogic#uploadFile(File)}
     * @param event
     */
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

    /**
     * Tries to save a file received by the server
     * @param name file name
     * @param data file data
     */
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

    /**
     * Updates the online usernames
     * @param clients usernames
     */
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

    /**
     * Updates the uploaded files
     * @param files uploaded files
     */
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

    /**
     * Shows an alert to the user
     * @param msg alert message
     * @param title alert title
     * @param type alert type
     */
    private static void showInfo(String msg, String title, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("SFP Client");
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Initializes variables on call and creates table columns
     * Sets double click event for file downloads
     * @param location
     * @param resources
     */
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
