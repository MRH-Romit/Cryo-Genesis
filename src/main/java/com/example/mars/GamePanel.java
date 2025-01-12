package com.example.mars;

import com.example.mars.keyHandle.KeyHandle;
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

    private int currentFrame = 0; // Current animation frame
    private long lastFrameTime = 0; // Last time the frame was updated
    private final long frameDuration = 100_000_000; // Frame duration (100ms)

    private tileManager tileM = new tileManager(this);  // TileManager initialization
    private KeyHandle keyH = new KeyHandle();

    @FXML
    private Canvas gameCanvas;

    private GraphicsContext gc; // Reference to the GraphicsContext

    private Image spriteSheet;
    private WritableImage characterSprite;

    private AnimationTimer gameLoop;

    @FXML
    public void initialize() {
        gc = gameCanvas.getGraphicsContext2D();

        // Load sprite sheet
        try (InputStream is = getClass().getResourceAsStream("/images/character.png")) {
            spriteSheet = new Image(is);
            // Set initial character sprite
            characterSprite = new WritableImage(spriteSheet.getPixelReader(), 0, 0, 48, 48);
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

        // Movement keys
        if (keyH.upPressed) {
            playerY = Math.max(playerY - playerSpeed, 0);
            isMoving = true;
        }
        if (keyH.downPressed) {
            playerY = Math.min(playerY + playerSpeed, screenHeight - tileSize);
            isMoving = true;
        }
        if (keyH.leftPressed) {
            playerX = Math.max(playerX - playerSpeed, 0);
            isMoving = true;
        }
        if (keyH.rightPressed) {
            playerX = Math.min(playerX + playerSpeed, screenWidth - tileSize);
            isMoving = true;
        }

        // Update frame for animation
        long currentTime = System.nanoTime();
        if (isMoving) {
            if (currentTime - lastFrameTime > frameDuration) {
                currentFrame = (currentFrame + 1) % 3; // Loop through 3 frames
                lastFrameTime = currentTime;
            }
            int row = calculateYDirectionOffset(); // Get correct row based on direction
            characterSprite = new WritableImage(
                    spriteSheet.getPixelReader(),
                    currentFrame * 48, // Select frame column
                    row * 48,          // Select frame row
                    48, 48             // Frame size
            );
        }

        // Handle attack
        if (keyH.attackPressed) {
            performAttack();
        }
    }

    private int calculateYDirectionOffset() {
        if (keyH.upPressed) return 3;   // Moving up row
        if (keyH.downPressed) return 4; // Moving down row
        if (keyH.leftPressed) return 5; // Moving left row
        if (keyH.rightPressed) return 5; // Moving right row
        return 0; // Default to idle
    }

    private void draw() {
        gc.clearRect(0, 0, screenWidth, screenHeight);
        tileM.draw(gc); // Draw tiles

        int characterWidth = tileSize * 2; // Double the width
        int characterHeight = tileSize * 2; // Double the height

        if (keyH.leftPressed) {
            gc.save();
            gc.scale(-1, 1); // Flip horizontally
            gc.drawImage(characterSprite, -playerX - characterWidth, playerY, characterWidth, characterHeight);
            gc.restore();
        } else {
            gc.drawImage(characterSprite, playerX, playerY, characterWidth, characterHeight);
        }
    }

    private void performAttack() {
        // Example attack animation logic
        characterSprite = new WritableImage(
                spriteSheet.getPixelReader(),
                currentFrame * 48,      // Attack frames
                6 * 48,                 // Attack row (6th row)
                48, 48                  // Frame size
        );
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
