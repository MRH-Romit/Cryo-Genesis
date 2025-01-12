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
    private int currentRow = 0; // Tracks the current animation row
    private long lastFrameTime = 0; // Last time the frame was updated
    private final long frameDuration = 100_000_000; // Frame duration (100ms)
    private boolean isAttacking = false; // Tracks if the player is attacking
    private boolean isDead = false; // Tracks if the player is dead

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

        // Handle movement logic
        if (keyH.upPressed) {
            playerY = Math.max(playerY - playerSpeed, 0);
            isMoving = true;
            currentRow = 5; // Row 5 for upside movement
        }
        if (keyH.downPressed) {
            playerY = Math.min(playerY + playerSpeed, screenHeight - tileSize);
            isMoving = true;
            currentRow = 3; // Row 3 for downside movement
        }
        if (keyH.rightPressed) {
            playerX = Math.min(playerX + playerSpeed, screenWidth - tileSize);
            isMoving = true;
            currentRow = 4; // Row 4 for right-side movement
        }
        if (keyH.leftPressed) {
            playerX = Math.max(playerX - playerSpeed, 0);
            isMoving = true;
            currentRow = 4; // Row 4 for left-side movement (flipped)
        }

        // Handle attack logic
        if (keyH.attackPressed && !isAttacking) {
            isAttacking = true;
            if (keyH.upPressed) currentRow = 8;          // Row 8 for upside attack
            else if (keyH.downPressed) currentRow = 6;   // Row 6 for downside attack
            else if (keyH.rightPressed) currentRow = 7;  // Row 7 for right-side attack
            else if (keyH.leftPressed) currentRow = 7;   // Row 7 for left-side attack (flipped)
            else currentRow = 6;                         // Default to downside attack if no movement
            currentFrame = 0; // Start attack animation from the first frame
        }

        long currentTime = System.nanoTime();
        if (isAttacking) {
            if (currentTime - lastFrameTime > frameDuration) {
                currentFrame++;
                lastFrameTime = currentTime;
            }
            if (currentFrame >= 3) { // Reset attack animation after 3 frames
                currentFrame = 0;
                isAttacking = false;
                keyH.attackPressed = false; // Reset the attack key flag
            }
        } else if (isMoving) {
            if (currentTime - lastFrameTime > frameDuration) {
                currentFrame = (currentFrame + 1) % 3; // Loop through 3 frames for movement
                lastFrameTime = currentTime;
            }
        } else if (!isDead) { // Idle animation
            if (keyH.upPressed) currentRow = 2;          // Row 2 for upside idle
            else if (keyH.downPressed) currentRow = 0;   // Row 0 for downside idle
            else if (keyH.rightPressed) currentRow = 1;  // Row 1 for right-side idle
            else if (keyH.leftPressed) currentRow = 1;   // Row 1 for left-side idle (flipped)

            if (currentTime - lastFrameTime > frameDuration) {
                currentFrame = (currentFrame + 1) % 3; // Loop through 3 frames for idle
                lastFrameTime = currentTime;
            }
        }

        // Handle death animation
        if (isDead) {
            currentRow = 9; // Row 9 for death
            currentFrame = 0; // Static frame for death
        }

        // Update the sprite based on the current frame and row
        characterSprite = new WritableImage(
                spriteSheet.getPixelReader(),
                currentFrame * 48,
                currentRow * 48,
                48, 48
        );
    }


    private int calculateMoveRow() {
        if (keyH.upPressed) return 5;
        if (keyH.downPressed) return 5;
        if (keyH.leftPressed) return 5;
        if (keyH.rightPressed) return 4;
        return 0;
    }

    private void draw() {
        gc.clearRect(0, 0, screenWidth, screenHeight);
        tileM.draw(gc);

        int characterWidth = tileSize * 2;
        int characterHeight = tileSize * 2;

        if (keyH.leftPressed || (isAttacking && currentRow == 7 && keyH.leftPressed)) {
            gc.save();
            gc.scale(-1, 1); // Flip horizontally
            gc.drawImage(
                    characterSprite,
                    -playerX - characterWidth,
                    playerY,
                    characterWidth,
                    characterHeight
            );
            gc.restore();
        } else {
            gc.drawImage(
                    characterSprite,
                    playerX,
                    playerY,
                    characterWidth,
                    characterHeight
            );
        }
    }

    public void triggerDeath() {
        isDead = true;
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
