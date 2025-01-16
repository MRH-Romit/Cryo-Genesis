package com.example.mars;

import com.example.mars.Entity.Slime;
import com.example.mars.Entity.Hero1;
import com.example.mars.keyHandle.KeyHandle;
import com.example.mars.tiles.tileManager;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;

public class GamePanel {
    private final int originalTileSize = 16;
    private final int scale = 5; // Adjusted scaling
    public final int maxScreenCol = 12;
    public final int maxScreenRow = 8;
    public final int tileSize = originalTileSize * scale; // 64
    private final int screenWidth = tileSize * maxScreenCol; // 1024
    private final int screenHeight = tileSize * maxScreenRow; // 768

    private Slime slime;
    private tileManager tileM = new tileManager(this);
    private KeyHandle keyH = new KeyHandle();
    private Hero1 hero;

    @FXML
    private Canvas gameCanvas;

    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    @FXML
    public void initialize() {
        gc = gameCanvas.getGraphicsContext2D();

        try (InputStream is = getClass().getResourceAsStream("/images/character.png")) {
            Image spriteSheet = new Image(is);
            hero = new Hero1(100, 100, 5, spriteSheet, tileSize); // Initialize hero with spriteSheet and tileSize
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (InputStream is = getClass().getResourceAsStream("/images/slime.png")) {
            Image slimeSpriteSheet = new Image(is);
            slime = new Slime(300, 300, 2, slimeSpriteSheet, tileSize); // Initialize slime
        } catch (Exception e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            gameCanvas.setFocusTraversable(true);
            gameCanvas.requestFocus();
            gameCanvas.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyH.keyPressedHandler);
            gameCanvas.getScene().addEventHandler(KeyEvent.KEY_RELEASED, keyH.keyReleasedHandler);
        });

        startGameLoop();
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;
            private final long frameDuration = 100_000_000; // 100ms

            @Override
            public void handle(long currentNanoTime) {
                if (currentNanoTime - lastUpdate >= frameDuration) {
                    update();
                    draw();
                    lastUpdate = currentNanoTime;
                }
            }
        };
        gameLoop.start();
    }

    private void update() {
        long currentNanoTime = System.nanoTime();

        // Update hero logic
        hero.update(
                keyH.upPressed,
                keyH.downPressed,
                keyH.leftPressed,
                keyH.rightPressed,
                keyH.attackPressed,
                currentNanoTime,
                screenWidth,
                screenHeight
        );

        // Reset attack key after handling it
        if (keyH.attackPressed) {
            keyH.attackPressed = false; // Reset to prevent continuous attack
        }

        // Update slime logic
        slime.update(currentNanoTime, screenWidth, screenHeight);
    }


    private void draw() {
        gc.clearRect(0, 0, screenWidth, screenHeight); // Clear the entire canvas before drawing

        tileM.draw(gc); // Draw tiles
        hero.draw(gc);  // Draw hero
        slime.draw(gc); // Draw slime

    }

    @FXML
    private void onPauseClick() {
        if (gameLoop != null) {
            gameLoop.stop();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pause.fxml"));
            Parent pauseRoot = loader.load();

            Scene pauseScene = new Scene(pauseRoot);
            Stage pauseStage = new Stage();
            pauseStage.setTitle("Pause Menu");
            pauseStage.setScene(pauseScene);
            pauseStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
//hjgjh