package com.example.mars;

import com.example.mars.Entity.Hero1;
import com.example.mars.Entity.Slime;
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
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class GamePanel {

    private final int originalTileSize = 16;
    private final int scale = 5; // Adjusted scaling
    public final int maxScreenCol = 12;
    public final int maxScreenRow = 8;
    public final int tileSize = originalTileSize * scale; // 80
    private final int screenWidth = tileSize * maxScreenCol; // 960
    private final int screenHeight = tileSize * maxScreenRow; // 640

    private Hero1 hero;
    private Slime slime;
    public final tileManager tileM = new tileManager(this);
    private final KeyHandle keyH = new KeyHandle();

    @FXML
    private Canvas gameCanvas;

    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    @FXML
    public void initialize() {
        gc = gameCanvas.getGraphicsContext2D();

        // Calculate the center of the map
        int centerX = (tileM.mapWidth * tileSize) / 2 - tileSize / 2;
        int centerY = (tileM.mapHeight * tileSize) / 2 - tileSize / 2;

        // Initialize the hero at the map's center
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
                    update(); // Update game state
                    draw();   // Render the game
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
            keyH.attackPressed = false; // Reset to prevent continuous attack
        }

        // Check the tile under the character
        int heroTileX = hero.getX() / tileSize;
        int heroTileY = hero.getY() / tileSize;

        if (heroTileX >= 0 && heroTileX < tileM.mapWidth && heroTileY >= 0 && heroTileY < tileM.mapHeight) {
            int currentTile = tileM.mapTileNum[heroTileX][heroTileY];

            if (currentTile == 5) {
                openNewFXML(); // Trigger new FXML loading
            }
        }
    }

    private void draw() {
        gc.clearRect(0, 0, screenWidth, screenHeight);

        // Center the camera on the hero
        int cameraX = hero.getX() - screenWidth / 2 + tileSize / 2;
        int cameraY = hero.getY() - screenHeight / 2 + tileSize / 2;

        // Constrain the camera to the map bounds
        cameraX = Math.max(0, Math.min(cameraX, tileM.mapWidth * tileSize - screenWidth));
        cameraY = Math.max(0, Math.min(cameraY, tileM.mapHeight * tileSize - screenHeight));

        // Draw map relative to the camera
        tileM.draw(gc, cameraX, cameraY);

        // Draw hero at the screen's center
        hero.draw(gc, screenWidth / 2 - tileSize / 2, screenHeight / 2 - tileSize / 2);
    }

    private void openNewFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mars/newScreen.fxml"));
            Parent root = loader.load();

            // Set the new scene
            Stage primaryStage = (Stage) gameCanvas.getScene().getWindow();
            primaryStage.setScene(new Scene(root));

            // Stop the game loop if needed
            gameLoop.stop();

            System.out.println("Transitioned to new screen!");
        } catch (IOException e) {
            e.printStackTrace();
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

    public void resumeGameLoop() {
        if (gameLoop != null) {
            gameLoop.start();
        }
    }
}
