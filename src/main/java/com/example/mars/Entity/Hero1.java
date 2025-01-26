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

    private int health = 100; // Add health attribute

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

    // Method to handle taking damage
    public void takeDamage(int damage) {
        health -= damage;
        System.out.println("Hero took " + damage + " damage. Current health: " + health);

        if (health <= 0) {
            System.out.println("Hero is dead!");
            // Handle hero death (e.g., restart game, show game over screen)
        }
    }

    // Getter for health
    public int getHealth() {
        return health;
    }

    public void update(boolean upPressed, boolean downPressed, boolean leftPressed,
                       boolean rightPressed, boolean attackPressed, long currentNanoTime,
                       int screenWidth, int screenHeight) {
        boolean isMoving = false;
        int newX = x;
        int newY = y;

        if (!isAttacking) {
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

            if (!checkCollision(newX, newY)) {
                x = Math.max(0, Math.min(newX, screenWidth - tileSize));
                y = Math.max(0, Math.min(newY, screenHeight - tileSize));
            }
        }

        if (attackPressed && !isAttacking) {
            isAttacking = true;
            if (isFacingUp) currentRow = 8;
            else if (isFacingDown) currentRow = 6;
            else currentRow = 7;
            currentFrame = 0;
        }

        if (currentNanoTime - lastFrameTime > frameDuration) {
            if (isAttacking) {
                currentFrame++;
                if (currentFrame >= 3) {
                    currentFrame = 0;
                    isAttacking = false;
                }
            } else if (isMoving) {
                currentFrame = (currentFrame + 1) % 3;
            } else {
                currentFrame = (currentFrame + 1) % 3;
                if (isFacingUp) currentRow = 2;
                else if (isFacingDown) currentRow = 0;
                else currentRow = 1;
            }
            lastFrameTime = currentNanoTime;

            characterSprite = new WritableImage(
                    spriteSheet.getPixelReader(),
                    currentFrame * 48,
                    currentRow * 48,
                    48, 48
            );
        }
    }

    private boolean checkCollision(int newX, int newY) {
        int hitboxSize = tileSize;

        int col1 = newX / tileSize;
        int row1 = newY / tileSize;
        int col2 = (newX + hitboxSize - 1) / tileSize;
        int row2 = newY / tileSize;
        int col3 = newX / tileSize;
        int row3 = (newY + hitboxSize - 1) / tileSize;
        int col4 = (newX + hitboxSize - 1) / tileSize;
        int row4 = (newY + hitboxSize - 1) / tileSize;

        return tileM.isCollision(col1, row1) ||
                tileM.isCollision(col2, row2) ||
                tileM.isCollision(col3, row3) ||
                tileM.isCollision(col4, row4);
    }

    public void draw(GraphicsContext gc, int screenX, int screenY) {
        if (!isFacingRight) {
            gc.save();
            gc.scale(-1, 1);
            gc.drawImage(characterSprite, -screenX - tileSize * 2, screenY, tileSize * 2, tileSize * 2);
            gc.restore();
        } else {
            gc.drawImage(characterSprite, screenX, screenY, tileSize * 2, tileSize * 2);
        }
    }
}
