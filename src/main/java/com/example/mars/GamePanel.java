package com.example.mars;

import com.example.mars.keyHandle.KeyHandle;
import com.example.mars.tiles.tileManager;
import com.example.mars.tiles.tileManager;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class GamePanel {
    private final int originalTileSize = 16;
    private final int scale = 4;
    public final int maxScreenCol = 12;
    public final int maxScreenRow = 8;
    public final int tileSize = originalTileSize * scale; // 64
    private final int screenWidth = tileSize * maxScreenCol; // 1024
    private final int screenHeight = tileSize * maxScreenRow; // 768

    private int playerX = 100;
    private int playerY = 100;
    private int playerSpeed = 2;

    private int idleFrame = 0; // Current frame for idle animation
    private long idleLastUpdate = 0; // Last time the idle frame updated
    private final long idleFrameDuration = 500_000_000; // 0.5 seconds per frame
    private int frameWidth = 85; // Calculated frame width
    private int frameHeight = 100; // Estimate from the sprite sheet rows
    private int walkingFrame = 0; // Current frame for walking animation
    private final int totalWalkingFrames = 4; // Total frames per walking animation direction
    private long lastFrameTime = 0; // Time of the last frame update
    private final long frameDuration = 100_000_000; // Duration for each frame (100 ms)


    tileManager tileM = new tileManager(   this);  // TileManager initialization
    private KeyHandle keyH = new KeyHandle();

    @FXML
    private Canvas gameCanvas;

    // Reference to the GraphicsContext
    private GraphicsContext gc;

    // Sprite data
    private Image spriteSheet;
    private WritableImage characterSprite;

    // Reference to our AnimationTimer so we can pause/stop it
    private AnimationTimer gameLoop;

    @FXML
    public void initialize() {
        gc = gameCanvas.getGraphicsContext2D();

        // Load sprite sheet
        try (InputStream is = getClass().getResourceAsStream("/images/character.png")) {
            spriteSheet = new Image(is);
            // Extract a sub-image (example: x=128, y=116, width=64, height=40)
            characterSprite = new WritableImage(spriteSheet.getPixelReader(), 128, 116, 64, 40);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ensure the canvas can receive key events
        Platform.runLater(() -> {
            gameCanvas.setFocusTraversable(true);
            gameCanvas.requestFocus();
            gameCanvas.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyH.keyPressedHandler);
            gameCanvas.getScene().addEventHandler(KeyEvent.KEY_RELEASED, keyH.keyReleasedHandler);
        });

        // Start the game loop
        startGameLoop();
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;
            private final long frameDuration = 1_000_000_000 / 60; // 60 FPS

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
        boolean isMoving = false;

        if (keyH.upPressed) {
            playerY = Math.max(playerY - playerSpeed, 0);
            isMoving = true;
        } else if (keyH.downPressed) {
            playerY = Math.min(playerY + playerSpeed, screenHeight - tileSize);
            isMoving = true;
        } else if (keyH.leftPressed) {
            playerX = Math.max(playerX - playerSpeed, 0);
            isMoving = true;
        } else if (keyH.rightPressed) {
            playerX = Math.min(playerX + playerSpeed, screenWidth - tileSize);
            isMoving = true;
        }

        long currentTime = System.nanoTime();
        if (isMoving) {
            if (currentTime - lastFrameTime > frameDuration) {
                walkingFrame = (walkingFrame + 1) % totalWalkingFrames; // Loop walking frames
                lastFrameTime = currentTime;
            }
            characterSprite = new WritableImage(
                    spriteSheet.getPixelReader(),
                    walkingFrame * frameWidth, // X position of frame
                    calculateYDirectionOffset(), // Y offset based on direction
                    frameWidth,
                    frameHeight
            );
        }
    }

    private int calculateYDirectionOffset() {
        if (keyH.upPressed) return frameHeight * 3;
        if (keyH.downPressed) return 0;
        if (keyH.leftPressed) return frameHeight * 1;
        if (keyH.rightPressed) return frameHeight * 2;
        return 0; // Default to the down direction if no input is detected
    }


    private void draw() {
        gc.clearRect(0, 0, screenWidth, screenHeight);
        tileM.draw(gc);
        gc.drawImage(characterSprite, playerX, playerY, tileSize, tileSize);
    }


    /**
     * Invoked when the user clicks the Pause button.
     * Opens pause.fxml in a new window and stops the game loop.
     */
    @FXML
    private void onPauseClick() {
        // Stop the game loop so the game is paused
        if (gameLoop != null) {
            gameLoop.stop();
        }

        // Load the pause menu in a new window
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
