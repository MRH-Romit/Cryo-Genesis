package com.example.mars;

import com.example.mars.Entity.Hero1;
import com.example.mars.Entity.Orc1;
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
import java.util.ArrayList;
import java.util.List;

public class GamePanel {
    private final int originalTileSize = 16;
    private final int scale = 5;
    public final int maxScreenCol = 12;
    public final int maxScreenRow = 8;
    public final int tileSize = originalTileSize * scale;
    private final int screenWidth = tileSize * maxScreenCol;
    private final int screenHeight = tileSize * maxScreenRow;
    private final List<Orc1> orcs = new ArrayList<>();

    private Hero1 hero;
    public final tileManager tileM = new tileManager(this);
    private final KeyHandle keyH = new KeyHandle();

    @FXML
    private Canvas gameCanvas;
    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    private int[] findGrassPosition() {
        for (int row = 0; row < tileM.mapHeight; row++) {
            for (int col = 0; col < tileM.mapWidth; col++) {
                if (tileM.getTileTypeAt(col, row) == 2) { // 2 is grass tile
                    return new int[]{col * tileSize + tileSize / 2, row * tileSize + tileSize / 2};
                }
            }
        }
        return new int[]{400, 300}; // fallback position
    }

    @FXML
    private void initialize() {
        gc = gameCanvas.getGraphicsContext2D();

        int centerX = (tileM.mapWidth * tileSize) / 2 - tileSize / 2;
        int centerY = (tileM.mapHeight * tileSize) / 2 - tileSize / 2;

        try (InputStream is = getClass().getResourceAsStream("/images/character.png")) {
            Image heroSprite = new Image(is);
            hero = new Hero1(centerX, centerY, 7, heroSprite, tileSize, tileM);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (InputStream runStream = getClass().getResourceAsStream("/images/orc1_run_full.png");
             InputStream attackStream = getClass().getResourceAsStream("/images/orc1_attack_full.png")) {

            Image runSpriteSheet = new Image(runStream);
            Image attackSpriteSheet = new Image(attackStream);

            int[] orcPosition = findGrassPosition();
            orcs.add(new Orc1(orcPosition[0], orcPosition[1], tileSize, runSpriteSheet, attackSpriteSheet, tileM));

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
                tileM.mapWidth * tileSize,
                tileM.mapHeight * tileSize
        );

        // Step 1: Check if the character is on tile[7] and go to the new screen
        int col = hero.getX() / tileSize;
        int row = hero.getY() / tileSize;
        if (tileM.getTileTypeAt(col, row) == 7) { // Tile [7] detected
            goToNewScreen();
            return; // Stop further updates to avoid conflicts after scene change
        }

        for (Orc1 orc : orcs) {
            double prevX = orc.getX();
            double prevY = orc.getY();

            orc.update(hero.getX(), hero.getY(), System.nanoTime());

            if (checkCollisionWithWalls(orc)) {
                orc.setPosition(prevX, prevY);
            }
        }

        if (keyH.attackPressed) {
            keyH.attackPressed = false;
        }
    }

    private boolean checkCollisionWithWalls(Orc1 orc) {
        int tileCol = (int) (orc.getX() / tileSize);
        int tileRow = (int) (orc.getY() / tileSize);

        // Check surrounding tiles
        for (int row = tileRow - 1; row <= tileRow + 1; row++) {
            for (int col = tileCol - 1; col <= tileCol + 1; col++) {
                if (row >= 0 && row < tileM.mapHeight && col >= 0 && col < tileM.mapWidth) {
                    if (tileM.isCollision(col, row)) {  // Use isCollision instead of getTileTypeAt
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void draw() {
        gc.clearRect(0, 0, screenWidth, screenHeight);

        int cameraX = hero.getX() - screenWidth / 2 + tileSize / 2;
        int cameraY = hero.getY() - screenHeight / 2 + tileSize / 2;

        cameraX = Math.max(0, Math.min(cameraX, tileM.mapWidth * tileSize - screenWidth));
        cameraY = Math.max(0, Math.min(cameraY, tileM.mapHeight * tileSize - screenHeight));

        tileM.draw(gc, cameraX, cameraY);

        int heroScreenX = Math.max(tileSize / 2, Math.min(hero.getX() - cameraX, screenWidth - tileSize / 2));
        int heroScreenY = Math.max(tileSize / 2, Math.min(hero.getY() - cameraY, screenHeight - tileSize / 2));
        hero.draw(gc, heroScreenX - tileSize / 2, heroScreenY - tileSize / 2);

        for (Orc1 orc : orcs) {
            orc.draw(gc, cameraX, cameraY);
        }
    }

    // Step 2: Navigate to the new screen
    private void goToNewScreen() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("newScreen.fxml"));
                Parent newScreenRoot = loader.load();
                Scene newScreenScene = new Scene(newScreenRoot);

                // Get the current stage
                Stage currentStage = (Stage) gameCanvas.getScene().getWindow();
                currentStage.setScene(newScreenScene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
