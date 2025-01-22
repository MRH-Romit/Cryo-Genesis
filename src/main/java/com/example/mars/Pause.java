package com.example.mars;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.Optional;

public class Pause {

    @FXML
    private Button resume_game;
    @FXML
    private Button pause_settings;
    @FXML
    private Button main_menu;
    @FXML
    private Button quit_game;

    @FXML
    private void onResumeGame() {
        // Logic to resume the game
        Stage currentStage = (Stage) resume_game.getScene().getWindow();
        currentStage.close(); // Close the pause menu to resume the game
    }

    @FXML
    private void onPauseSettings() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("settings.fxml"));
            Scene settingsScene = new Scene(fxmlLoader.load(), 850, 550);

            Stage settingsStage = new Stage();
            settingsStage.setScene(settingsScene);
            settingsStage.setTitle("Settings");
            settingsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onMainMenu() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("home.fxml"));
            Scene homeScene = new Scene(fxmlLoader.load(), 850, 550);

            Stage currentStage = (Stage) main_menu.getScene().getWindow();
            currentStage.setScene(homeScene);
            currentStage.setTitle("Home Page");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onQuitGame() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit Game");
        alert.setHeaderText("Are you sure you want to quit?");
        alert.setContentText("Select Yes to quit or No to stay.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Exit the game
            Stage currentStage = (Stage) quit_game.getScene().getWindow();
            currentStage.close();
        } else {
            // Stay in the game
            alert.close();
        }
    }
}
