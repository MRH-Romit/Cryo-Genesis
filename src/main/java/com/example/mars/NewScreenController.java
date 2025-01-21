package com.example.mars;

import com.example.mars.Entity.Hero1;
import com.example.mars.keyHandle.KeyHandle;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;

import java.io.InputStream;

public class NewScreenController {

    private final int screenWidth = 960; // Screen width
    private final int screenHeight = 640; // Screen height
    private final int tileSize = 80; // Tile size for consistent scaling

    private Hero1 hero;
    private final KeyHandle keyH = new KeyHandle();
    private AnimationTimer gameLoop;
    private GraphicsContext gc;

    @FXML
    private Canvas gameCanvas;

    @FXML
    public void initialize() {
        gc = gameCanvas.getGraphicsContext2D();

        // Initialize the hero at the screen's center
        int centerX = screenWidth / 2 - tileSize / 2;
        int centerY = screenHeight / 2 - tileSize / 2;

        try (InputStream is = getClass().getResourceAsStream("/images/character.png")) {
            Image heroSprite = new Image(is);
            hero = new Hero1(centerX, centerY, 7, heroSprite, tileSize);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set up keyboard input
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
                    update(); // Update character movement
                    draw();   // Render the character
                    lastUpdate = currentNanoTime;
                }
            }
        };
        gameLoop.start();
    }

    private void update() {
        // Update hero logic
        hero.update(
                keyH.upPressed,
                keyH.downPressed,
                keyH.leftPressed,
                keyH.rightPressed,
                keyH.attackPressed,
                System.nanoTime(),
                screenWidth,
                screenHeight
        );

        // Reset attack key after handling it
        if (keyH.attackPressed) {
            keyH.attackPressed = false;
        }
    }

    private void draw() {
        gc.clearRect(0, 0, screenWidth, screenHeight); // Clear the canvas

        // Draw hero at its current position
        hero.draw(gc, hero.getX(), hero.getY());
    }
}
