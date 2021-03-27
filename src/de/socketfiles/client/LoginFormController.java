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

public class LoginFormController {

    private static HomeFormController homeFormController;

    @FXML
    private Button loginBtn;

    @FXML
    private TextField usernameField, addressField;

    @FXML
    private Button helpBtn;

    @FXML
    private MaskerPane loadingIndicator;

    @FXML
    void onHelp(ActionEvent event) {
        showInfo("Um sich mit dem Server zu verbinden, müssen Sie einen einzigartigen Nutzernamen verwenden.",
                "Information",
                Alert.AlertType.INFORMATION);
    }

    @FXML
    void onLogin(ActionEvent event) {
        loadingIndicator.setVisible(true);
        login();
    }

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

    private static void showInfo(String msg, String title, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("SFP Client");
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait();

    }

    public static void showInfoLater(String msg, String title, Alert.AlertType type) {
        Platform.runLater(() -> {
            showInfo(msg, title, type);
        });
    }
}
