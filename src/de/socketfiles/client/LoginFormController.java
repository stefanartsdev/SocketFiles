package de.socketfiles.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

public class LoginFormController {

    @FXML
    private Button loginBtn;

    @FXML
    private TextField usernameField;

    @FXML
    private Button helpBtn;

    @FXML
    void onHelp(ActionEvent event) {
        showInfo("Um sich mit dem Server zu verbinden, müssen Sie einen einzigartigen Nutzernamen verwenden.", "Information");
    }

    @FXML
    void onLogin(ActionEvent event) {

    }

    private void showInfo(String msg, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("SFP Client");
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.setAlertType(Alert.AlertType.INFORMATION);
        alert.show();

    }

}
