package de.socketfiles.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class GUI extends Application {



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("SFP Client");
        URL file = getClass().getResource("/de/socketfiles/client/clientLogin.fxml");
        assert file != null;
        Parent root = FXMLLoader.load(file);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
