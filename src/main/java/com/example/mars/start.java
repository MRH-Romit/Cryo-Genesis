package com.example.mars;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class start {

    @FXML
    private Label Label1; // Displays "Loading..." or similar message

    @FXML
    private Label Label2; // Displays the percentage (e.g., "25%")

    @FXML
    private Rectangle Rectangle1; // Filled portion of the loading bar

    @FXML
    private Rectangle Rectangle2; // Background of the loading bar

    private double progress = 0; // Progress value (0 to 1)
    private Timeline timeline; // Declare timeline at the class level

    @FXML
    public void initialize() {
        Rectangle1.setWidth(0); // Start with an empty loading bar
        double maxWidth = Rectangle2.getWidth(); // Get the width of the background rectangle

        // Timeline to simulate loading progress
        timeline = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            progress += 0.01; // Increment progress
            if (progress > 1) progress = 1; // Prevent progress from exceeding 1 (100%)
            double currentWidth = maxWidth * progress; // Calculate new width based on progress
            Rectangle1.setWidth(currentWidth); // Update the width of Rectangle1
            Label2.setText((int) (progress * 100) + "%"); // Update percentage label

            if (progress >= 1) {
                Label1.setText("Loading Complete!"); // Update message
                Label2.setText("100%");

                timeline.stop(); // Stop the timeline when progress reaches 100%

                // Introduce a delay before transitioning to the home page
                PauseTransition pause = new PauseTransition(Duration.millis(120));
                pause.setOnFinished(event1 -> switchToHomePage()); // Switch to home page after the delay
                pause.play();
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE); // Set to run indefinitely, we control stop manually
        timeline.play(); // Start the timeline
    }

    // Method to switch to the home page
    @SuppressWarnings("CallToPrintStackTrace")
    private void switchToHomePage() {
        System.out.println("Switching to Home Page..."); // Debug line
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("home.fxml"));
            Scene homeScene = new Scene(fxmlLoader.load(), 816, 540);

            Stage currentStage = (Stage) Label1.getScene().getWindow();
            currentStage.setScene(homeScene);
            currentStage.setTitle("Home Page");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
