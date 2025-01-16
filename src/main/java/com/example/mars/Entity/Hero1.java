package com.example.mars.Entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Hero1 {
    private int x, y; // Position
    private int speed; // Movement speed
    private int tileSize;
    private Image spriteSheet;
    private WritableImage characterSprite;

    private int currentFrame = 0; // Animation frame
    private int currentRow = 0; // Animation row
    private long lastFrameTime = 0;
    private final long frameDuration = 100_000_000; // 100ms per frame
    private boolean isAttacking = false;

    private boolean isFacingRight = true;
    private boolean isFacingUp = false;
    private boolean isFacingDown = true; // Default facing direction

    public Hero1(int startX, int startY, int speed, Image spriteSheet, int tileSize) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.spriteSheet = spriteSheet;
        this.tileSize = tileSize;
        this.characterSprite = new WritableImage(spriteSheet.getPixelReader(), 0, 0, 48, 48);
    }

    public void update(boolean upPressed, boolean downPressed, boolean leftPressed, boolean rightPressed, boolean attackPressed, long currentNanoTime, int screenWidth, int screenHeight) {
        boolean isMoving = false;

        // Movement logic
        if (upPressed) {
            y = Math.max(y - speed, 0);
            isMoving = true;
            currentRow = 5; // Row for up movement
            isFacingUp = true;
            isFacingDown = false;
        }
        if (downPressed) {
            y = Math.min(y + speed, screenHeight - tileSize);
            isMoving = true;
            currentRow = 3; // Row for down movement
            isFacingDown = true;
            isFacingUp = false;
        }
        if (rightPressed) {
            x = Math.min(x + speed, screenWidth - tileSize);
            isMoving = true;
            currentRow = 4; // Row for right movement
            isFacingRight = true;
        }
        if (leftPressed) {
            x = Math.max(x - speed, 0);
            isMoving = true;
            currentRow = 4; // Row for left movement (flipped)
            isFacingRight = false;
        }

        // Attack logic
        if (attackPressed && !isAttacking) {
            isAttacking = true;
            if (upPressed) currentRow = 8;          // Row 8 for up attack
            else if (downPressed) currentRow = 6;   // Row 6 for down attack
            else if (rightPressed) currentRow = 7;  // Row 7 for right attack
            else if (leftPressed) currentRow = 7;   // Row 7 for left attack (flipped)
            else if (isFacingUp) currentRow = 8;    // Default up attack
            else if (isFacingDown) currentRow = 6;  // Default down attack
            else if (isFacingRight) currentRow = 7; // Default right attack
            else currentRow = 7;                    // Default left attack
            currentFrame = 0; // Reset animation to start
        }

        // Animation frames logic
        if (isAttacking) {
            if (currentNanoTime - lastFrameTime > frameDuration) {
                currentFrame++;
                lastFrameTime = currentNanoTime;
            }
            if (currentFrame >= 3) { // Reset after 3 attack frames
                currentFrame = 0;
                isAttacking = false; // End the attack
            }
        } else if (isMoving) {
            if (currentNanoTime - lastFrameTime > frameDuration) {
                currentFrame = (currentFrame + 1) % 3; // Loop through 3 frames for movement
                lastFrameTime = currentNanoTime;
            }
        } else { // Idle animation
            if (currentNanoTime - lastFrameTime > frameDuration) {
                currentFrame = (currentFrame + 1) % 3; // Loop through 3 idle frames
                lastFrameTime = currentNanoTime;
            }
            // Set idle row based on the last direction faced
            if (isFacingUp) currentRow = 2;
            else if (isFacingDown) currentRow = 0;
            else if (isFacingRight) currentRow = 1;
            else currentRow = 1; // Left idle
        }

        // Update sprite for the current frame
        characterSprite = new WritableImage(
                spriteSheet.getPixelReader(),
                currentFrame * 48,
                currentRow * 48,
                48, 48
        );
    }

    public void draw(GraphicsContext gc) {
        int characterWidth = tileSize * 2;
        int characterHeight = tileSize * 2;

        if (!isFacingRight) {
            gc.save();
            gc.scale(-1, 1);
            gc.drawImage(characterSprite, -x - characterWidth, y, characterWidth, characterHeight);
            gc.restore();
        } else {
            gc.drawImage(characterSprite, x, y, characterWidth, characterHeight);
        }
    }
}
//jtlklk