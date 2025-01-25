package com.example.mars;

import com.example.mars.Entity.Hero1;
import com.example.mars.Entity.Orc1;
import com.example.mars.keyHandle.KeyHandle;
import com.example.mars.sound.SoundManager;
import com.example.mars.tiles.tileManager;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
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

    // Tutorial UI elements (must match IDs in your FXML)
    @FXML
    private AnchorPane tutorialOverlay;
    @FXML
    private Label tutorialLabel;
    @FXML
    private ImageView tutorialCharacterView;
    @FXML
    private Button tutorialContinueBtn;

    /**
     * The single initialize() method called by JavaFX after FXML is loaded.
     */
    @FXML
    private void initialize() {
        // Get the GraphicsContext from our Canvas
        gc = gameCanvas.getGraphicsContext2D();

        // Calculate a central position for the hero
        int centerX = (tileM.mapWidth * tileSize) / 2 - tileSize / 2;
        int centerY = (tileM.mapHeight * tileSize) / 2 - tileSize / 2;

        // Load the hero sprite
        try (InputStream is = getClass().getResourceAsStream("/images/character.png")) {
            Image heroSprite = new Image(is);
            hero = new Hero1(centerX, centerY, 7, heroSprite, tileSize, tileM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load the orc sprites
        try (InputStream runStream = getClass().getResourceAsStream("/images/orc1_run_full.png");
             InputStream attackStream = getClass().getResourceAsStream("/images/orc1_attack_full.png")) {

            Image runSpriteSheet = new Image(runStream);
            Image attackSpriteSheet = new Image(attackStream);

            int[] orcPosition = findGrassPosition();
            orcs.add(new Orc1(orcPosition[0], orcPosition[1], tileSize, runSpriteSheet, attackSpriteSheet, tileM));

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Play background music
        SoundManager.playLandingPageMusic("/audio/GamePanel_BG - Copy.mp3");

        // Let the canvas receive key focus
        Platform.runLater(() -> {
            gameCanvas.setFocusTraversable(true);
            gameCanvas.requestFocus();
            gameCanvas.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyH.keyPressedHandler);
            gameCanvas.getScene().addEventHandler(KeyEvent.KEY_RELEASED, keyH.keyReleasedHandler);
        });

        // Start the game loop
        startGameLoop();

        // Optionally show a tutorial message
        showTutorialMessage("If you're looking for new challenges to beat, just tap here!");
    }

    /**
     * Finds a grass tile (type=2) on the map and returns its center coordinates.
     */
    private int[] findGrassPosition() {
        for (int row = 0; row < tileM.mapHeight; row++) {
            for (int col = 0; col < tileM.mapWidth; col++) {
                if (tileM.getTileTypeAt(col, row) == 2) { // 2 is grass tile
                    return new int[]{col * tileSize + tileSize / 2, row * tileSize + tileSize / 2};
                }
            }
        }
        return new int[]{400, 300}; // Fallback if no grass tile found
    }

    /**
     * Shows the tutorial overlay with a given message.
     */
    public void showTutorialMessage(String message) {
        tutorialLabel.setText(message);
        tutorialOverlay.setVisible(true);
    }

    /**
     * Hides the tutorial overlay.
     */
    public void hideTutorialMessage() {
        tutorialOverlay.setVisible(false);
    }

    /**
     * Called when user clicks "Okay, got it!" in the overlay.
     */
    @FXML
    private void onTutorialContinue() {
        hideTutorialMessage();
    }

    /**
     * Starts the main game loop with an AnimationTimer.
     */
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

    /**
     * Updates the game state (hero, orcs, collisions, etc.).
     */
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

    /**
     * Checks collisions between an orc and walls around it.
     */
    private boolean checkCollisionWithWalls(Orc1 orc) {
        int tileCol = (int) (orc.getX() / tileSize);
        int tileRow = (int) (orc.getY() / tileSize);

        for (int row = tileRow - 1; row <= tileRow + 1; row++) {
            for (int col = tileCol - 1; col <= tileCol + 1; col++) {
                if (row >= 0 && row < tileM.mapHeight && col >= 0 && col < tileM.mapWidth) {
                    if (tileM.isCollision(col, row)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Draws the game world, hero, orcs, etc.
     */
    private void draw() {
        gc.clearRect(0, 0, screenWidth, screenHeight);

        // Center camera on hero
        int cameraX = hero.getX() - screenWidth / 2 + tileSize / 2;
        int cameraY = hero.getY() - screenHeight / 2 + tileSize / 2;

        // Clamp camera to map boundaries
        cameraX = Math.max(0, Math.min(cameraX, tileM.mapWidth * tileSize - screenWidth));
        cameraY = Math.max(0, Math.min(cameraY, tileM.mapHeight * tileSize - screenHeight));

        // Draw map
        tileM.draw(gc, cameraX, cameraY);

        // Draw hero
        int heroScreenX = Math.max(tileSize / 2, Math.min(hero.getX() - cameraX, screenWidth - tileSize / 2));
        int heroScreenY = Math.max(tileSize / 2, Math.min(hero.getY() - cameraY, screenHeight - tileSize / 2));
        hero.draw(gc, heroScreenX - tileSize / 2, heroScreenY - tileSize / 2);

        // Draw orcs
        for (Orc1 orc : orcs) {
            orc.draw(gc, cameraX, cameraY);
        }
    }

    /**
     * Pauses the game when pause button is clicked.
     */
    @FXML
    private void onPauseClick() {
        if (gameLoop != null) {
            gameLoop.stop();
        }

        // Stop game music on pause
        SoundManager.stopMusic();

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

    /**
     * Resume the game loop and music.
     */
    public void resumeGameLoop() {
        if (gameLoop != null) {
            gameLoop.start();
        }
        SoundManager.playLandingPageMusic("/audio/GamePanel_BG - Copy.mp3");
    }
}
