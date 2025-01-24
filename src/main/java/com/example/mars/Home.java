package com.example.mars;

import com.example.mars.sound.SoundManager;
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
        // Play home page music
        SoundManager.playLandingPageMusic("/audio/home_soundtrack.mp3");
    }

    @FXML
    private void onPlayGame() {
        SoundManager.stopMusic(); // Stop home music before switching scenes
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
            System.out.println("Opening chat windows...");

            FXMLLoader clientLoader = new FXMLLoader(getClass().getResource("/com/example/mars/Chat.fxml"));
            Scene clientScene = new Scene(clientLoader.load(), 850, 550);
            Stage clientStage = new Stage();
            clientStage.setScene(clientScene);
            clientStage.setTitle("Client Support Chat");
            clientStage.show();

            FXMLLoader agentLoader = new FXMLLoader(getClass().getResource("/com/example/mars/AgentChat.fxml"));
            Scene agentScene = new Scene(agentLoader.load(), 850, 550);
            Stage agentStage = new Stage();
            agentStage.setScene(agentScene);
            agentStage.setTitle("Agent Support Chat");
            agentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onMarketplace() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("marketplace.fxml"));
            Scene marketplaceScene = new Scene(fxmlLoader.load(), 850, 550);
            Stage currentStage = (Stage) marketplace.getScene().getWindow();
            currentStage.setScene(marketplaceScene);
            currentStage.setTitle("Marketplace");
        } catch (IOException e) {
            e.printStackTrace();
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
