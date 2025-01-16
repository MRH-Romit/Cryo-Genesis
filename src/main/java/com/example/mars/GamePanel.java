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
    private final int scale = 5; // Adjusted scaling
    public final int maxScreenCol = 12;
    public final int maxScreenRow = 8;
    public final int tileSize = originalTileSize * scale; // 64
    private final int screenWidth = tileSize * maxScreenCol; // 1024
    private final int screenHeight = tileSize * maxScreenRow; // 768

    private int playerX = 100;
    private int playerY = 100;
    private int playerSpeed = 6;

    private int currentFrame = 0; // Animation frame
    private int currentRow = 0; // Animation row
    private long lastFrameTime = 0;
    private final long frameDuration = 100_000_000; // 100ms
    private boolean isAttacking = false;

    private tileManager tileM = new tileManager(this);
    private KeyHandle keyH = new KeyHandle();

    @FXML
    private Canvas gameCanvas;

    private GraphicsContext gc;
    private Image spriteSheet;
    private WritableImage characterSprite;

    private AnimationTimer gameLoop;

    @FXML
    public void initialize() {
        gc = gameCanvas.getGraphicsContext2D();

        try (InputStream is = getClass().getResourceAsStream("/images/character.png")) {
            spriteSheet = new Image(is);
            characterSprite = new WritableImage(spriteSheet.getPixelReader(), 0, 0, 48, 48);
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

    private boolean isFacingRight = true; // Track the direction the character is facing
    private boolean isFacingUp = false;  // Track if the character is facing up
    private boolean isFacingDown = true; // Default facing direction

    private void update() {
        boolean isMoving = false;

        // Handle movement logic
        if (keyH.upPressed) {
            playerY = Math.max(playerY - playerSpeed, 0);
            isMoving = true;
            currentRow = 5; // Row 5 for upside movement
            isFacingUp = true;
            isFacingDown = false;
        }
        if (keyH.downPressed) {
            playerY = Math.min(playerY + playerSpeed, tileM.mapHeight * tileSize - tileSize);
            isMoving = true;
            currentRow = 3; // Row 3 for downside movement
            isFacingDown = true;
            isFacingUp = false;
        }
        if (keyH.rightPressed) {
            playerX = Math.min(playerX + playerSpeed, tileM.mapWidth * tileSize - tileSize);
            isMoving = true;
            currentRow = 4; // Row 4 for right-side movement
            isFacingRight = true;
        }
        if (keyH.leftPressed) {
            playerX = Math.max(playerX - playerSpeed, 0);
            isMoving = true;
            currentRow = 4; // Row 4 for left-side movement (flipped)
            isFacingRight = false;
        }

        // Handle attack logic
        if (keyH.attackPressed && !isAttacking) {
            isAttacking = true;
            if (keyH.upPressed) currentRow = 8;          // Row 8 for upside attack
            else if (keyH.downPressed) currentRow = 6;   // Row 6 for downside attack
            else if (keyH.rightPressed) currentRow = 7;  // Row 7 for right-side attack
            else if (keyH.leftPressed) currentRow = 7;   // Row 7 for left-side attack (flipped)
            else if (isFacingUp) currentRow = 8;         // Default to upside attack if facing up
            else if (isFacingDown) currentRow = 6;       // Default to downside attack if facing down
            else if (isFacingRight) currentRow = 7;      // Default to right-side attack
            else currentRow = 7;                         // Default to left-side attack
            currentFrame = 0; // Start attack animation from the first frame
        }

        // Handle animation frame updates
        long currentTime = System.nanoTime();
        if (isAttacking) {
            if (currentTime - lastFrameTime > frameDuration) {
                currentFrame++;
                lastFrameTime = currentTime;
            }
            if (currentFrame >= 3) { // Reset attack animation after 3 frames
                currentFrame = 0;
                isAttacking = false;
                keyH.attackPressed = false;
            }
        } else if (isMoving) {
            if (currentTime - lastFrameTime > frameDuration) {
                currentFrame = (currentFrame + 1) % 3; // Loop through 3 frames for movement
                lastFrameTime = currentTime;
            }
        } else {
            if (currentTime - lastFrameTime > frameDuration) {
                currentFrame = (currentFrame + 1) % 3; // Loop through 3 frames for idle animation
                lastFrameTime = currentTime;
            }
            // Set idle row based on the last direction faced
            if (isFacingUp) currentRow = 2;         // Row 2 for upside idle
            else if (isFacingDown) currentRow = 0;  // Row 0 for downside idle
            else if (isFacingRight) currentRow = 1; // Row 1 for right-side idle
            else currentRow = 1;                    // Row 1 for left-side idle (flipped handled in draw)
        }

        // Update the sprite based on the current frame and row
        characterSprite = new WritableImage(
                spriteSheet.getPixelReader(),
                currentFrame * 48,
                currentRow * 48,
                48, 48
        );
    }

    private void draw() {
        gc.clearRect(0, 0, screenWidth, screenHeight); // Clear the entire canvas before drawing

        // Calculate the camera's top-left position
        int cameraX = playerX - screenWidth / 2 + tileSize / 2;
        int cameraY = playerY - screenHeight / 2 + tileSize / 2;

        // Clamp the camera to prevent showing out-of-bounds areas
        cameraX = Math.max(0, Math.min(cameraX, tileM.mapWidth * tileSize - screenWidth));
        cameraY = Math.max(0, Math.min(cameraY, tileM.mapHeight * tileSize - screenHeight));

        // Draw the tile map adjusted for the camera position
        tileM.draw(gc, cameraX, cameraY);

        int characterWidth = tileSize * 2;
        int characterHeight = tileSize * 2;

        // Draw the character at the center of the screen
        int screenX = (screenWidth - characterWidth) / 2;
        int screenY = (screenHeight - characterHeight) / 2;

        // Flip the sprite if the character is facing left
        if (!isFacingRight) {
            gc.save();
            gc.scale(-1, 1);
            gc.drawImage(characterSprite, -screenX - characterWidth, screenY, characterWidth, characterHeight);
            gc.restore();
        } else {
            gc.drawImage(characterSprite, screenX, screenY, characterWidth, characterHeight);
        }
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
