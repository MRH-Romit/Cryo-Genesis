package com.example.mars;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;

import java.io.InputStream;

public class GamePanelController {
    private final int originalTileSize = 16;
    private final int scale = 4;
    private final int tileSize = originalTileSize * scale; // 64
    private final int screenWidth = tileSize * 16; // 1024
    private final int screenHeight = tileSize * 12; // 768

    private int playerX = 100;
    private int playerY = 100;
    private int playerSpeed = 2;

    private KeyHandle keyH = new KeyHandle();

    @FXML
    private Canvas gameCanvas;

    private GraphicsContext gc;
    private Image spriteSheet;
    private WritableImage characterSprite;

    @FXML
    public void initialize() {
        gc = gameCanvas.getGraphicsContext2D();

        // Load sprite sheet
        try (InputStream is = getClass().getResourceAsStream("/images/character.png")) {
            spriteSheet = new Image(is);
            // Extract a sub-image (e.g., x=64, y=320, width=64, height=40)
            characterSprite = new WritableImage(spriteSheet.getPixelReader(), 128, 116, 64, 40);
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
        AnimationTimer gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;
            private final long frameDuration = 1_000_000_000 / 60;

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
        // Update player position and sprite based on direction
        if (keyH.upPressed) {
            playerY = Math.max(playerY - playerSpeed, 0);
            // Extract the "up" frame
            characterSprite = new WritableImage(spriteSheet.getPixelReader(), 0, 0, 128, 112);
        }
        if (keyH.downPressed) {
            playerY = Math.min(playerY + playerSpeed, screenHeight - tileSize);
            // Extract the "down" frame
            characterSprite = new WritableImage(spriteSheet.getPixelReader(), 128, 0, 128, 112);
        }
        if (keyH.leftPressed) {
            playerX = Math.max(playerX - playerSpeed, 0);
            // Extract the "left" frame
            characterSprite = new WritableImage(spriteSheet.getPixelReader(), 256, 0, 128, 112);
        }
        if (keyH.rightPressed) {
            playerX = Math.min(playerX + playerSpeed, screenWidth - tileSize);
            // Extract the "right" frame
            characterSprite = new WritableImage(spriteSheet.getPixelReader(), 384, 0, 128, 112);
        }
    }

    private void draw() {
        gc.clearRect(0, 0, screenWidth, screenHeight);
        gc.drawImage(characterSprite, playerX, playerY, tileSize, tileSize);
    }
}
