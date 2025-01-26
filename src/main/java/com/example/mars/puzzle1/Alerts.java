package com.example.mars.puzzle1;

import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;

public class Alerts {

    public void showRules() {

        Alert rulesAlert = new Alert(Alert.AlertType.INFORMATION, "- Only 1 disc can be moved at a time\n- No larger disc can be placed on top of smaller\n  disc");
        rulesAlert.setTitle("Rules");
        rulesAlert.setHeaderText("The only 2 rules :");
        rulesAlert.show();
    }

    public void showInvalidMove() {
        Alert warning = new Alert(Alert.AlertType.ERROR, "INVALID MOVE!");
        warning.setHeaderText("Your Move Is :");
        warning.setTitle("♥♥♥");
        warning.show();
    }

    public void showWin() {
        Alert wonGame = new Alert(Alert.AlertType.WARNING, "Game Will Restart Now");
        wonGame.setTitle("☺☺☺");
        wonGame.setHeaderText("Congrats you won!");
        wonGame.setGraphic(new ImageView((this.getClass().getResource("RajiniK.png").toString())));
        wonGame.showAndWait();
    }
}
