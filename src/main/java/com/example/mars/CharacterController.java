package com.example.mars;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class CharacterController {

    @FXML
    private ImageView characterImage; // Character image view

    @FXML
    private Button onBack; // Back button

    // List of character image file paths
    private final String[] characters = {
            "@../../../images/MaleCharacter.jpg",
            "@../../../images/MaleCharacter2.jpg",
            "@../../../images/FemaleCharacter.jpg"
    };

    private int currentCharacterIndex = 0; // To keep track of the current character index

    /**
     * Handles the left button click to display the previous character.
     */
    @FXML
    private void handleLeftButton() {
        // Decrease the character index and loop back if necessary
        currentCharacterIndex = (currentCharacterIndex - 1 + characters.length) % characters.length;
        updateCharacterImage();
    }

    /**
     * Handles the right button click to display the next character.
     */
    @FXML
    private void handleRightButton() {
        // Increase the character index and loop back if necessary
        currentCharacterIndex = (currentCharacterIndex + 1) % characters.length;
        updateCharacterImage();
    }

    /**
     * Handles the select button click to confirm character selection.
     */
    @FXML
    private void handleSelectButton() {
        // Show a confirmation alert with the selected character
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Character Selection");
        alert.setHeaderText(null);
        alert.setContentText("You have selected character: " + (currentCharacterIndex + 1));
        alert.showAndWait();
    }

    /**
     * Updates the character image based on the currentCharacterIndex.
     */
    private void updateCharacterImage() {
        characterImage.setImage(new Image(characters[currentCharacterIndex]));
    }

    /**
     * Handles the back button click to navigate to the home page.
     */
    @FXML
    private void onBack() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("home.fxml"));
            Scene homeScene = new Scene(fxmlLoader.load(), 850, 550);
            Stage currentStage = (Stage) onBack.getScene().getWindow();
            currentStage.setScene(homeScene);
            currentStage.setTitle("Home Page");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
