package com.example.mars.Entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Hero1 {
    private int x, y;
    private int speed;
    private int tileSize;
    private Image spriteSheet;
    private WritableImage characterSprite;

    private int currentFrame = 0;
    private int currentRow = 0;
    private long lastFrameTime = 0;
    private final long frameDuration = 100_000_000;
    private boolean isAttacking = false;

    private boolean isFacingRight = true;
    private boolean isFacingUp = false;
    private boolean isFacingDown = true;

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

        if (upPressed) {
            y = Math.max(y - speed, 0);
            isMoving = true;
            currentRow = 5;
            isFacingUp = true;
            isFacingDown = false;
        }
        if (downPressed) {
            y = Math.min(y + speed, screenHeight - tileSize);
            isMoving = true;
            currentRow = 3;
            isFacingDown = true;
            isFacingUp = false;
        }
        if (rightPressed) {
            x = Math.min(x + speed, screenWidth - tileSize);
            isMoving = true;
            currentRow = 4;
            isFacingRight = true;
        }
        if (leftPressed) {
            x = Math.max(x - speed, 0);
            isMoving = true;
            currentRow = 4;
            isFacingRight = false;
        }

        if (attackPressed && !isAttacking) {
            isAttacking = true;
            if (upPressed) currentRow = 8;
            else if (downPressed) currentRow = 6;
            else if (rightPressed) currentRow = 7;
            else if (leftPressed) currentRow = 7;
            else if (isFacingUp) currentRow = 8;
            else if (isFacingDown) currentRow = 6;
            else if (isFacingRight) currentRow = 7;
            else currentRow = 7;
            currentFrame = 0;
        }

        if (isAttacking) {
            if (currentNanoTime - lastFrameTime > frameDuration) {
                currentFrame++;
                lastFrameTime = currentNanoTime;
            }
            if (currentFrame >= 3) {
                currentFrame = 0;
                isAttacking = false;
            }
        } else if (isMoving) {
            if (currentNanoTime - lastFrameTime > frameDuration) {
                currentFrame = (currentFrame + 1) % 3;
                lastFrameTime = currentNanoTime;
            }
        } else {
            if (currentNanoTime - lastFrameTime > frameDuration) {
                currentFrame = (currentFrame + 1) % 3;
                lastFrameTime = currentNanoTime;
            }
            if (isFacingUp) currentRow = 2;
            else if (isFacingDown) currentRow = 0;
            else if (isFacingRight) currentRow = 1;
            else currentRow = 1;
        }

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
