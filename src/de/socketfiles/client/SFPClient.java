package de.socketfiles.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Main class of client
 * Manages UI and {@link de.socketfiles.client.Client}
 */
public class SFPClient extends Application {

    /**
     * Instance of class
     */
    private static SFPClient gui;

    /**
     * main method to run client
     * @param args program arguments
     */
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

    /**
     * Returns the login window scene
     * @return JavaFX Scene
     * @throws IOException if file cannot be loaded
     */
    private Scene getLoginWindow() throws IOException {
        URL file = getClass().getResource("/de/socketfiles/client/clientLogin.fxml");
        assert file != null;
        return new Scene(FXMLLoader.load(file));
    }

    /**
     * Getter for the instance of this class
     * @return SFPClient instance
     */
    public static SFPClient getGUI() {
        return gui;
    }
}
