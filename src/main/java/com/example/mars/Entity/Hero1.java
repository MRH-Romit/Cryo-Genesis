package com.example.mars.Entity;

import com.example.mars.tiles.tileManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Hero1 {
    private int x, y;
    private int speed;
    private int tileSize;
    private Image spriteSheet;
    private WritableImage characterSprite;
    private tileManager tileM;

    private int currentFrame = 0;
    private int currentRow = 0;
    private long lastFrameTime = 0;
    private final long frameDuration = 100_000_000;
    private boolean isAttacking = false;

    private boolean isFacingRight = true;
    private boolean isFacingUp = false;
    private boolean isFacingDown = true;

    public boolean isAttackingDone() {
        return !isAttacking;
    }

    public void resetAttack() {
        isAttacking = false;
        currentFrame = 0;
    }

    public Hero1(int startX, int startY, int speed, Image spriteSheet, int tileSize, tileManager tileM) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.spriteSheet = spriteSheet;
        this.tileSize = tileSize;
        this.tileM = tileM;
        this.characterSprite = new WritableImage(spriteSheet.getPixelReader(), 0, 0, 48, 48);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    private boolean checkCollision(int newX, int newY) {
        // Check all four corners of the hero's hitbox
        int hitboxSize = tileSize; // You can adjust this for a smaller/larger hitbox

        // Top left corner
        int col1 = newX / tileSize;
        int row1 = newY / tileSize;

        // Top right corner
        int col2 = (newX + hitboxSize - 1) / tileSize;
        int row2 = newY / tileSize;

        // Bottom left corner
        int col3 = newX / tileSize;
        int row3 = (newY + hitboxSize - 1) / tileSize;

        // Bottom right corner
        int col4 = (newX + hitboxSize - 1) / tileSize;
        int row4 = (newY + hitboxSize - 1) / tileSize;

        return tileM.isCollision(col1, row1) ||
                tileM.isCollision(col2, row2) ||
                tileM.isCollision(col3, row3) ||
                tileM.isCollision(col4, row4);
    }

    public void update(boolean upPressed, boolean downPressed, boolean leftPressed,
                       boolean rightPressed, boolean attackPressed, long currentNanoTime,
                       int screenWidth, int screenHeight) {
        boolean isMoving = false;
        int newX = x;
        int newY = y;

        if (upPressed) {
            newY = y - speed;
            isMoving = true;
            currentRow = 5;
            isFacingUp = true;
            isFacingDown = false;
        }
        if (downPressed) {
            newY = y + speed;
            isMoving = true;
            currentRow = 3;
            isFacingDown = true;
            isFacingUp = false;
        }
        if (rightPressed) {
            newX = x + speed;
            isMoving = true;
            currentRow = 4;
            isFacingRight = true;
        }
        if (leftPressed) {
            newX = x - speed;
            isMoving = true;
            currentRow = 4;
            isFacingRight = false;
        }

        // Check collision before updating position
        if (!checkCollision(newX, newY)) {
            x = Math.max(0, Math.min(newX, screenWidth - tileSize));
            y = Math.max(0, Math.min(newY, screenHeight - tileSize));
        }

        if (attackPressed && !isAttacking) {
            isAttacking = true;
            if (isFacingUp) currentRow = 8;
            else if (isFacingDown) currentRow = 6;
            else currentRow = 7;
            currentFrame = 0;
        }

        // Animation logic
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
            else currentRow = 1;
        }

        // Update sprite
        characterSprite = new WritableImage(
                spriteSheet.getPixelReader(),
                currentFrame * 48,
                currentRow * 48,
                48, 48
        );
    }

    public void draw(GraphicsContext gc, int screenX, int screenY) {
        int characterWidth = tileSize * 2;
        int characterHeight = tileSize * 2;

        if (!isFacingRight) {
            gc.save();
            gc.scale(-1, 1);
            gc.drawImage(characterSprite, -screenX - characterWidth, screenY, characterWidth, characterHeight);
            gc.restore();
        } else {
            gc.drawImage(characterSprite, screenX, screenY, characterWidth, characterHeight);
        }
    }
}