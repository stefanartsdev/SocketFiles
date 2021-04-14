package de.socketfiles.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.MaskerPane;

import java.io.IOException;
import java.net.URL;

/**
 * Controller class for the JavaFX login window
 */
public class LoginFormController {

    @FXML
    private TextField usernameField, addressField;

    @FXML
    private MaskerPane loadingIndicator;

    /**
     * Action event for the help button
     * Shows an info alert on how to use the program
     * @param event
     */
    @FXML
    void onHelp(ActionEvent event) {
        showInfo("Um sich mit dem Server zu verbinden, müssen Sie einen einzigartigen Nutzernamen verwenden.",
                "Information",
                Alert.AlertType.INFORMATION);
    }

    /**
     * Action event for the login button
     * Shows the loading indicator and calls the method login()
     * @param event
     */
    @FXML
    void onLogin(ActionEvent event) {
        loadingIndicator.setVisible(true);
        login();
    }

    /**
     * Tries to connect the user to the specified server address and port
     */
    private void login() {
        String address;
        int port = 1805;
        String[] addressLine = addressField.getText().split(":");
        if (addressLine.length == 1) {
            address = addressField.getText();
        } else if (addressLine.length == 2) {
            address = addressLine[0];
            try {
                port = Integer.parseInt(addressLine[1]);
            } catch (NumberFormatException e) {
            }
        } else {
            return;
        }
        int finalPort = port;
        Thread t = new Thread(() -> {
            try {
                    ClientLogic.tryLogin(address, finalPort, usernameField.getText());
            } catch (IOException | ClassNotFoundException e) {
                    loadingIndicator.setVisible(false);
                    Platform.runLater(() -> {
                        showInfo("Die Verbindung konnte nicht hergestellt werden: " + e,
                                "Verbindung fehlgeschlagen",
                                Alert.AlertType.ERROR);
                    });
                    return;
            }
            Platform.runLater(() -> {
                loadingIndicator.setVisible(false);
                URL file = getClass().getResource("/de/socketfiles/client/clientHome.fxml");
                assert file != null;
                Stage primaryStage = (Stage)loadingIndicator.getScene().getWindow();
                try {
                    Scene s = new Scene(FXMLLoader.load(file));
                    primaryStage.setScene(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        });
        t.start();


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
}
