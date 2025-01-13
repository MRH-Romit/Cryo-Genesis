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
    private int playerSpeed = 5;

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

    private void update() {
        boolean isMoving = false;

        if (keyH.upPressed) {
            playerY = Math.max(playerY - playerSpeed, 0);
            isMoving = true;
            currentRow = 5;
        }
        if (keyH.downPressed) {
            playerY = Math.min(playerY + playerSpeed, screenHeight - tileSize);
            isMoving = true;
            currentRow = 3;
        }
        if (keyH.rightPressed) {
            playerX = Math.min(playerX + playerSpeed, screenWidth - tileSize);
            isMoving = true;
            currentRow = 4;
        }
        if (keyH.leftPressed) {
            playerX = Math.max(playerX - playerSpeed, 0);
            isMoving = true;
            currentRow = 4;
        }

        if (keyH.attackPressed && !isAttacking) {
            isAttacking = true;
            currentRow = 6;
            currentFrame = 0;
        }

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
            currentFrame = 0;
        }

        characterSprite = new WritableImage(
                spriteSheet.getPixelReader(),
                currentFrame * 48,
                currentRow * 48,
                48, 48
        );
    }

    private void draw() {
        gc.clearRect(0, 0, screenWidth, screenHeight);  // Clear the entire canvas before drawing

        tileM.draw(gc);

        int characterWidth = tileSize * 2;
        int characterHeight = tileSize * 2;

        if (keyH.leftPressed || (isAttacking && currentRow == 6)) {
            gc.save();
            gc.scale(-1, 1);
            gc.drawImage(characterSprite, -playerX - characterWidth, playerY, characterWidth, characterHeight);
            gc.restore();
        } else {
            gc.drawImage(characterSprite, playerX, playerY, characterWidth, characterHeight);
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
