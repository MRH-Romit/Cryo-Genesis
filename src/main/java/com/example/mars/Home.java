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
    private Button character; // Character button

    @FXML
    public void initialize() {
        // Initialize logic if needed
    }

    @FXML
    private void onPlayGame() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GamePanel.fxml"));
            Scene gameScene = new Scene(fxmlLoader.load(), 850, 550);
            Stage currentStage = (Stage) play_game.getScene().getWindow();
            currentStage.setScene(gameScene);
            currentStage.setTitle("Game Page");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCharacter() {
        try {
            // Load marketplace.fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("marketplace.fxml"));
            Scene marketplaceScene = new Scene(fxmlLoader.load(), 600, 400);

            // Create a new stage for the marketplace window
            Stage marketplaceStage = new Stage();
            marketplaceStage.setScene(marketplaceScene);
            marketplaceStage.setTitle("Marketplace");
            marketplaceStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onContinue() {
        // Implement logic for continue button if needed
    }
}