package com.example.mars;

import com.example.mars.keyHandle.KeyHandle;
import com.example.mars.tiles.tileManager;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class GamePanel {

    private final int originalTileSize = 16;
    private final int scale = 5;
    public final int maxScreenCol = 12;
    public final int maxScreenRow = 8;
    public final int tileSize = originalTileSize * scale;
    private final int screenWidth = tileSize * maxScreenCol;
    private final int screenHeight = tileSize * maxScreenRow;

    private int playerX = 100;
    private int playerY = 100;
    private final int playerSpeed = 6;

    private int currentFrame = 0;
    private int currentRow = 0;
    private long lastFrameTime = 0;
    private final long frameDuration = 100_000_000;
    private boolean isAttacking = false;

    private final tileManager tileM = new tileManager(this);
    private final KeyHandle keyH = new KeyHandle();

    @FXML
    private Canvas gameCanvas;

    private GraphicsContext gc;
    private Image spriteSheet;
    private WritableImage characterSprite;

    private AnimationTimer gameLoop;
    private boolean isFacingRight = true;
    private boolean isFacingUp = false;
    private boolean isFacingDown = true;

    @FXML
    public void initialize() {
        gc = gameCanvas.getGraphicsContext2D();

        loadSpriteSheet();

        Platform.runLater(() -> {
            gameCanvas.setFocusTraversable(true);
            gameCanvas.requestFocus();
            gameCanvas.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyH.keyPressedHandler);
            gameCanvas.getScene().addEventHandler(KeyEvent.KEY_RELEASED, keyH.keyReleasedHandler);
        });

        tileM.loadMap(); // Ensure the map is loaded before starting the game loop

        // Ensure the first frame is drawn immediately
        update();
        draw();

        startGameLoop();
    }

    private void loadSpriteSheet() {
        try (InputStream is = getClass().getResourceAsStream("/images/character.png")) {
            spriteSheet = new Image(is);
            characterSprite = new WritableImage(spriteSheet.getPixelReader(), 0, 0, 48, 48);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long currentNanoTime) {
                if (lastUpdate == 0) lastUpdate = currentNanoTime; // Handle first frame
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

        // Movement logic
        if (keyH.upPressed) {
            playerY = Math.max(playerY - playerSpeed, 0);
            isMoving = true;
            currentRow = 5;
            isFacingUp = true;
            isFacingDown = false;
        }
        if (keyH.downPressed) {
            playerY = Math.min(playerY + playerSpeed, tileM.mapHeight * tileSize - tileSize);
            isMoving = true;
            currentRow = 3;
            isFacingDown = true;
            isFacingUp = false;
        }
        if (keyH.rightPressed) {
            playerX = Math.min(playerX + playerSpeed, tileM.mapWidth * tileSize - tileSize);
            isMoving = true;
            currentRow = 4;
            isFacingRight = true;
        }
        if (keyH.leftPressed) {
            playerX = Math.max(playerX - playerSpeed, 0);
            isMoving = true;
            currentRow = 4;
            isFacingRight = false;
        }

        // Attack logic
        if (keyH.attackPressed && !isAttacking) {
            isAttacking = true;
            currentRow = determineAttackRow();
            currentFrame = 0;
        }

        handleAnimation(isMoving);
    }

    private int determineAttackRow() {
        if (keyH.upPressed) return 8;
        if (keyH.downPressed) return 6;
        if (keyH.rightPressed || (isFacingRight && !isFacingUp && !isFacingDown)) return 7;
        return 7; // Default to left attack
    }

    private void handleAnimation(boolean isMoving) {
        long currentTime = System.nanoTime();
        if (isAttacking) {
            if (currentTime - lastFrameTime > frameDuration) {
                currentFrame++;
                lastFrameTime = currentTime;
            }
            if (currentFrame >= 3) {
                currentFrame = 0;
                isAttacking = false;
                keyH.attackPressed = false;
            }
        } else if (isMoving) {
            if (currentTime - lastFrameTime > frameDuration) {
                currentFrame = (currentFrame + 1) % 3;
                lastFrameTime = currentTime;
            }
        } else {
            if (currentTime - lastFrameTime > frameDuration) {
                currentFrame = (currentFrame + 1) % 3;
                lastFrameTime = currentTime;
            }
            if (isFacingUp) currentRow = 2;
            else if (isFacingDown) currentRow = 0;
            else currentRow = 1;
        }

        updateSprite();
    }

    private void updateSprite() {
        characterSprite = new WritableImage(
                spriteSheet.getPixelReader(),
                currentFrame * 48,
                currentRow * 48,
                48, 48
        );
    }

    private void draw() {
        gc.clearRect(0, 0, screenWidth, screenHeight);

        int cameraX = playerX - screenWidth / 2 + tileSize / 2;
        int cameraY = playerY - screenHeight / 2 + tileSize / 2;

        cameraX = Math.max(0, Math.min(cameraX, tileM.mapWidth * tileSize - screenWidth));
        cameraY = Math.max(0, Math.min(cameraY, tileM.mapHeight * tileSize - screenHeight));

        tileM.draw(gc, cameraX, cameraY);

        int characterWidth = tileSize * 2;
        int characterHeight = tileSize * 2;

        int screenX = (screenWidth - characterWidth) / 2;
        int screenY = (screenHeight - characterHeight) / 2;

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
