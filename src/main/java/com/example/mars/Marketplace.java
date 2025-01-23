package com.example.mars;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class Marketplace {

    @FXML
    private Button backButton;
    @FXML
    private TextField searchBar;
    @FXML
    private Button buyCharacter1, buyCharacter2, buyCharacter3;
    @FXML
    private Button buyMap1, buyMap2, buyMap3;
    @FXML
    private ImageView character1Image, character2Image, character3Image;
    @FXML
    private ImageView map1Image, map2Image, map3Image;

    /**
     * Handle "Buy Now" button clicks for characters and maps.
     */
    @FXML
    private void handleBuyNow(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String itemName;

        if (clickedButton == buyCharacter1) {
            itemName = "Character 1";
        } else if (clickedButton == buyCharacter2) {
            itemName = "Character 2";
        } else if (clickedButton == buyCharacter3) {
            itemName = "Character 3";
        } else if (clickedButton == buyMap1) {
            itemName = "Map 1";
        } else if (clickedButton == buyMap2) {
            itemName = "Map 2";
        } else if (clickedButton == buyMap3) {
            itemName = "Map 3";
        } else {
            itemName = "Unknown Item";
        }

        // Show success message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Purchase Successful");
        alert.setHeaderText(null);
        alert.setContentText("Congratulations! Your purchase of " + itemName + " was successful.");
        alert.showAndWait();
    }

    /**
     * Handle search functionality.
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        String query = searchBar.getText().trim().toLowerCase();

        // Show/hide items based on the search query
        boolean showCharacters = query.contains("char");
        boolean showMaps = query.contains("map");

        // Show or hide character items
        character1Image.setVisible(showCharacters);
        buyCharacter1.setVisible(showCharacters);
        character2Image.setVisible(showCharacters);
        buyCharacter2.setVisible(showCharacters);
        character3Image.setVisible(showCharacters);
        buyCharacter3.setVisible(showCharacters);

        // Show or hide map items
        map1Image.setVisible(showMaps);
        buyMap1.setVisible(showMaps);
        map2Image.setVisible(showMaps);
        buyMap2.setVisible(showMaps);
        map3Image.setVisible(showMaps);
        buyMap3.setVisible(showMaps);

        // If the query is empty or does not match, show all items
        if (query.isEmpty()) {
            character1Image.setVisible(true);
            buyCharacter1.setVisible(true);
            character2Image.setVisible(true);
            buyCharacter2.setVisible(true);
            character3Image.setVisible(true);
            buyCharacter3.setVisible(true);

            map1Image.setVisible(true);
            buyMap1.setVisible(true);
            map2Image.setVisible(true);
            buyMap2.setVisible(true);
            map3Image.setVisible(true);
            buyMap3.setVisible(true);
        }
    }

    /**
     * Handle "Back" button click to go to the home.fxml page.
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Load the home.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mars/home.fxml"));
            Parent homeRoot = loader.load();

            // Get the current stage and set the scene to home.fxml
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(homeRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Show an error message if the home.fxml file cannot be loaded
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Unable to load the home page.");
            alert.showAndWait();
        }
    }
}
