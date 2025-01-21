package com.example.mars;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class NewScreenController {
    @FXML
    private Button backButton;

    @FXML
    private void onBackClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mars/newScreen.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene gameScene = new Scene(root);
            stage.setScene(gameScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
