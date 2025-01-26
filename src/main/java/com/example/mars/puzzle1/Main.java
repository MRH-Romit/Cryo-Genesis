package com.example.mars.puzzle1;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("tower_of_hanoi_puzzle.fxml"));
        primaryStage.setTitle("Tower of Hanoi");
        primaryStage.setResizable(false);
        Image icon = new Image("Icon.jpg");
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.println("Application Closed.");
                System.exit(0);
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
