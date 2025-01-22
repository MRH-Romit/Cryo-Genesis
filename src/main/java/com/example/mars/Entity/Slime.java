package com.example.mars.Entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.Random;

public class Slime {
    private int x, y; // Position
    private int speed;
    private int tileSize;
    private Image spriteSheet;
    private WritableImage slimeSprite;

    private int currentFrame = 0; // Animation frame
    private int currentRow = 0; // Animation row
    private long lastFrameTime = 0;
    private final long frameDuration = 100_000_000; // 100ms per frame
    private boolean isFacingRight = true;

    private Random random = new Random();
    private long lastMoveTime = 0;
    private final long moveCooldown = 1_000_000_000; // 1 second cooldown for changing direction

    public Slime(int startX, int startY, int speed, Image spriteSheet, int tileSize) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.spriteSheet = spriteSheet;
        this.tileSize = tileSize;
        this.slimeSprite = new WritableImage(spriteSheet.getPixelReader(), 0, 0, 16, 16);
    }

    public void update(long currentNanoTime, int screenWidth, int screenHeight) {
        boolean isMoving = false;

        // Random movement logic
        if (currentNanoTime - lastMoveTime > moveCooldown) {
            int direction = random.nextInt(4); // Random direction: 0 = up, 1 = down, 2 = left, 3 = right
            switch (direction) {
                case 0: // Up
                    y = Math.max(y - speed, 0);
                    isMoving = true;
                    currentRow = 0; // Adjust based on sprite sheet
                    break;
                case 1: // Down
                    y = Math.min(y + speed, screenHeight - tileSize);
                    isMoving = true;
                    currentRow = 1; // Adjust based on sprite sheet
                    break;
                case 2: // Left
                    x = Math.max(x - speed, 0);
                    isMoving = true;
                    currentRow = 2; // Adjust based on sprite sheet
                    isFacingRight = false;
                    break;
                case 3: // Right
                    x = Math.min(x + speed, screenWidth - tileSize);
                    isMoving = true;
                    currentRow = 3; // Adjust based on sprite sheet
                    isFacingRight = true;
                    break;
            }
            lastMoveTime = currentNanoTime;
        }

        // Animation frame logic
        if (currentNanoTime - lastFrameTime > frameDuration) {
            currentFrame = (currentFrame + 1) % 3; // Loop through 3 animation frames
            lastFrameTime = currentNanoTime;
        }

        // Update sprite for the current frame
        slimeSprite = new WritableImage(
                spriteSheet.getPixelReader(),
                currentFrame * 16, // Frame width
                currentRow * 16,   // Row height
                16,                // Frame width
                16                 // Frame height
        );
    }

    public void draw(GraphicsContext gc) {
        int slimeWidth = tileSize;
        int slimeHeight = tileSize;

        gc.save(); // Save the current graphics context

        if (!isFacingRight) {
            gc.translate(x + slimeWidth, y); // Move to the slime's position
            gc.scale(-1, 1); // Flip the sprite horizontally
            gc.drawImage(slimeSprite, 0, 0, slimeWidth, slimeHeight);
        } else {
            gc.drawImage(slimeSprite, x, y, slimeWidth, slimeHeight);
        }

        gc.restore(); // Restore the graphics context
    }
}
//j