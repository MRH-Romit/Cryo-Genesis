package com.example.mars;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

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
        try {
            // Load the GamePanel FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GamePanel.fxml"));
            Scene gameScene = new Scene(fxmlLoader.load(), 850, 550);

            Stage currentStage = (Stage) resume_game.getScene().getWindow();
            currentStage.setScene(gameScene);
            currentStage.setTitle("Game Page");

            // Retrieve the GamePanel instance and resume the game loop
            GamePanel gamePanel = fxmlLoader.getController();
            gamePanel.resumeGameLoop();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        alert.setHeaderText(null); // No header for a cleaner look
        alert.setContentText("Are you sure you want to quit the game?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No", ButtonType.CANCEL.getButtonData());

        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            // Close everything (terminate the application)
            Platform.exit();
            System.exit(0);
        } else {
            // Do nothing and stay in the game
            alert.close();
        }
    }
    }

