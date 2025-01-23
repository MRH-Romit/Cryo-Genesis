package com.example.mars.main;

import com.example.mars.Socket.ChatServer; // Import your ChatServer
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Start ChatServer in a separate thread
        Thread serverThread = new Thread(() -> {
            try {
                ChatServer.main(null); // Start the ChatServer
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.setDaemon(true); // Ensure the server thread stops when the application exits
        serverThread.start();

        // Load the main UI
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/mars/start.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 850, 550);
        stage.setTitle("Start");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(); // Launch the JavaFX application
    }
}
