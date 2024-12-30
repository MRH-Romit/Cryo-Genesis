package com.example.mars;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.IOException;

public class Home {

    @FXML
    private Button play_game;
    @FXML
    private Button continueButton; // if needed for later use

    @FXML
    public void initialize() {
        // If needed, you can set up event handlers here or directly in FXML.
        // Example (if not using onAction in FXML):
        // play_game.setOnAction(event -> switchToGameScene());
    }

    // This method will be called when "Play Game" button is clicked.
    // To connect this to the button in FXML, you have two options:
    //
    // Option 1: Set the button's onAction in FXML:
    // <Button fx:id="play_game" onAction="#onPlayGame" ... />
    //
    // Option 2: Programmatically set it in initialize():
    // play_game.setOnAction(event -> onPlayGame());

    @FXML
    private void onPlayGame() {
        // When the user clicks the Play Game button, load the game scene
        try {
            // Load the Game.fxml file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GamePanel.fxml"));
            Scene gameScene = new Scene(fxmlLoader.load(), 816, 540);

            // Get the current stage from the button's scene
            Stage currentStage = (Stage) play_game.getScene().getWindow();

            // Switch the scene to the game scene
            currentStage.setScene(gameScene);
            currentStage.setTitle("Game Page");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onContinue() {
        // Implement logic for continue button if needed
    }
}
