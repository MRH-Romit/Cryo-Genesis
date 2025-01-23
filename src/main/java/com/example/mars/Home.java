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
    private Button settings; // Settings button
    @FXML
    private Button support; // Support button
    @FXML
    private Button back_button;
    @FXML
    private Button marketplace; // Back button

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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("character.fxml"));
            Scene marketplaceScene = new Scene(fxmlLoader.load(), 850, 550);
            Stage marketplaceStage = new Stage();
            marketplaceStage.setScene(marketplaceScene);
            marketplaceStage.setTitle("Character");
            marketplaceStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   @FXML
    private void onSettings() {
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
    private void onSupport() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("support.fxml"));
            Scene supportScene = new Scene(fxmlLoader.load(), 850, 550);
            Stage supportStage = new Stage();
            supportStage.setScene(supportScene);
            supportStage.setTitle("Support");
            supportStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onMarketplace() {
        try {
            System.out.println("Marketplace button clicked!");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("marketplace.fxml"));
            if (fxmlLoader == null) {
                System.out.println("FXML file not found!");
            }
            Scene marketplaceScene = new Scene(fxmlLoader.load(), 850, 550);
            Stage currentStage = (Stage) marketplace.getScene().getWindow();
            currentStage.setScene(marketplaceScene);
            currentStage.setTitle("Marketplace");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading marketplace.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("home.fxml"));
            Scene homeScene = new Scene(fxmlLoader.load(), 850, 550);
            Stage currentStage = (Stage) back_button.getScene().getWindow();
            currentStage.setScene(homeScene);
            currentStage.setTitle("Home Page");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
