package de.socketfiles.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SFPClient extends Application {

    private static SFPClient gui;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        gui = this;
        primaryStage.setTitle("SFP Client");
        primaryStage.setScene(getLoginWindow());
        primaryStage.setOnCloseRequest(event -> {
            ClientLogic.close();
        });
        primaryStage.show();
    }

    private Scene getLoginWindow() throws IOException {
        URL file = getClass().getResource("/de/socketfiles/client/clientLogin.fxml");
        assert file != null;
        return new Scene(FXMLLoader.load(file));
    }

    public static SFPClient getGUI() {
        return gui;
    }
}
