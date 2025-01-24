package com.example.mars;

import com.example.mars.Entity.Hero1;
import com.example.mars.keyHandle.KeyHandle;
import com.example.mars.tiles.TileManager2;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;

import java.io.InputStream;

public class NewScreenController {

    private final int screenWidth = 960;
    private final int screenHeight = 640;
    private final int tileSize = 80;

    private Hero1 hero;
    private final KeyHandle keyH = new KeyHandle();
    private AnimationTimer gameLoop;
    private GraphicsContext gc;
    private TileManager2 tileManager2;

    @FXML
    private Canvas gameCanvas;

    @FXML
    public void initialize() {
        try {
            gc = gameCanvas.getGraphicsContext2D();

            // Initialize TileManager2
            tileManager2 = new TileManager2(tileSize);

            // Initialize the hero
            int centerX = screenWidth / 2 - tileSize / 2;
            int centerY = screenHeight / 2 - tileSize / 2;

            try (InputStream is = getClass().getResourceAsStream("/images/character.png")) {
                if (is == null) throw new Exception("Hero image not found!");
                Image heroSprite = new Image(is);
                hero = new Hero1(centerX, centerY, 7, heroSprite, tileSize);
            }

            // Set up keyboard input
            Platform.runLater(() -> {
                gameCanvas.setFocusTraversable(true);
                gameCanvas.requestFocus();
                gameCanvas.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyH.keyPressedHandler);
                gameCanvas.getScene().addEventHandler(KeyEvent.KEY_RELEASED, keyH.keyReleasedHandler);
            });

            startGameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;
            private final long frameDuration = 100_000_000;

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

        // Reset attack key
        if (keyH.attackPressed) {
            keyH.attackPressed = false;
        }

        // Constrain the hero within bounds
        hero.setX(Math.max(0, Math.min(hero.getX(), screenWidth - tileSize)));
        hero.setY(Math.max(0, Math.min(hero.getY(), screenHeight - tileSize)));
    }

    private void draw() {
        gc.clearRect(0, 0, screenWidth, screenHeight);

        // Draw the map
        tileManager2.draw(gc, 0, 0);

        // Draw the hero
        hero.draw(gc, hero.getX(), hero.getY());
    }
}
