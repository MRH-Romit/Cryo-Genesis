package com.example.mars.Entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.Random;

public class Slime {
    private int x, y; // Slime position
    private int speed; // Movement speed
    private int tileSize;
    private Image spriteSheet;
    private WritableImage currentSprite;

    private int currentFrame = 0; // Animation frame
    private int currentRow = 0; // Animation row
    private long lastMoveTime = 0;
    private long lastFrameTime = 0;
    private final long moveDuration = 500_000_000; // 500ms between movements
    private final long frameDuration = 100_000_000; // 100ms per animation frame
    private Random random;

    public Slime(int startX, int startY, int speed, Image spriteSheet, int tileSize) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.spriteSheet = spriteSheet;
        this.tileSize = tileSize;
        this.random = new Random();
        this.currentSprite = new WritableImage(spriteSheet.getPixelReader(), 0, 0, 48, 48);
    }

    public void update(long currentNanoTime, int screenWidth, int screenHeight) {
        // Handle random movement
        if (currentNanoTime - lastMoveTime >= moveDuration) {
            int direction = random.nextInt(4); // 0: up, 1: down, 2: left, 3: right

            switch (direction) {
                case 0: // Move up
                    y = Math.max(y - speed, 0);
                    currentRow = 1; // Row for upward movement
                    break;
                case 1: // Move down
                    y = Math.min(y + speed, screenHeight - tileSize);
                    currentRow = 0; // Row for downward movement
                    break;
                case 2: // Move left
                    x = Math.max(x - speed, 0);
                    currentRow = 2; // Row for left movement
                    break;
                case 3: // Move right
                    x = Math.min(x + speed, screenWidth - tileSize);
                    currentRow = 3; // Row for right movement
                    break;
            }

            lastMoveTime = currentNanoTime;
        }

        // Handle animation frames
        if (currentNanoTime - lastFrameTime > frameDuration) {
            currentFrame = (currentFrame + 1) % 3; // Loop through 3 animation frames
            lastFrameTime = currentNanoTime;
        }

        // Update sprite for the current animation frame
        currentSprite = new WritableImage(
                spriteSheet.getPixelReader(),
                currentFrame * 48,
                currentRow * 48,
                48, 48
        );
    }

    public void draw(GraphicsContext gc) {
        int characterWidth = tileSize;
        int characterHeight = tileSize;

        // Clear overlapping sprites and ensure one sprite is drawn at a time
        gc.drawImage(currentSprite, x, y, characterWidth, characterHeight);
    }
}

