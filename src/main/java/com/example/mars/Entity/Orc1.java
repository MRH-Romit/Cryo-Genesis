package com.example.mars.Entity;

import com.example.mars.tiles.tileManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Orc1 {
    private double x, y;
    private int size;
    private Image runSpriteSheet;
    private Image attackSpriteSheet;
    private WritableImage currentSprite;
    private int currentFrame = 0;
    private int framesPerRow = 8;
    private int totalFrames = 8;
    private boolean isAttacking = false;
    private long lastFrameTime = 0;
    private final long frameDuration = 100_000_000;
    private int detectionRange = 200; // Reduced for closer detection
    private int attackRange = 60;     // Reduced for closer attack
    private double moveSpeed = 2.5;   // Adjusted for smoother movement
    private Direction facing = Direction.DOWN;
    private tileManager tileM;
    private boolean isMoving = false;
    private int health = 5;  // Orc dies after 5 hits
    private boolean isDead = false;
    private int minimumDistance = 40; // Minimum distance between Orc and Hero


    private enum Direction {
        UP(0),
        RIGHT(1),
        DOWN(2),
        LEFT(3);

        final int row;
        Direction(int row) {
            this.row = row;
        }
    }
    // Add getter for isDead
    public boolean isDead() {
        return isDead;
    }

    public void takeHit() {
        health--;
        if (health <= 0) {
            isDead = true;
        }
    }

    public void update(int heroX, int heroY, long currentNanoTime) {
        if (isDead) {
            return; // Don't update if dead
        }

        if (tileM == null) {
            return;
        }

        // Calculate distance and direction to hero
        double dx = heroX - x;
        double dy = heroY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Update facing direction based on hero position
        if (Math.abs(dx) > Math.abs(dy)) {
            facing = dx > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            facing = dy > 0 ? Direction.DOWN : Direction.UP;
        }

        if (distance <= detectionRange) {
            if (distance <= attackRange && distance >= minimumDistance) {
                // Close enough to attack but not too close
                isAttacking = true;
                isMoving = false;
            } else if (distance < minimumDistance) {
                // Too close, move away
                double angle = Math.atan2(dy, dx);
                double moveX = -Math.cos(angle) * moveSpeed; // Move in opposite direction
                double moveY = -Math.sin(angle) * moveSpeed;

                double newX = x + moveX;
                double newY = y + moveY;

                if (isValidPosition(newX, newY)) {
                    x = newX;
                    y = newY;
                }
            } else {
                // Move towards hero until minimum distance
                isAttacking = false;
                isMoving = true;

                double angle = Math.atan2(dy, dx);
                double moveX = Math.cos(angle) * moveSpeed;
                double moveY = Math.sin(angle) * moveSpeed;

                double newX = x + moveX;
                double newY = y + moveY;

                if (isValidPosition(newX, newY)) {
                    x = newX;
                    y = newY;
                }
            }
        } else {
            isAttacking = false;
            isMoving = false;
        }

        // Update animation
        if (currentNanoTime - lastFrameTime > frameDuration) {
            if (isAttacking) {
                currentFrame = (currentFrame + 1) % 8;
            } else if (isMoving) {
                currentFrame = (currentFrame + 1) % 8;
            } else {
                currentFrame = 0;
            }
            lastFrameTime = currentNanoTime;
            updateCurrentSprite();
        }
    }

    private boolean isValidPosition(double newX, double newY) {
        int newTileCol = (int)(newX / size);
        int newTileRow = (int)(newY / size);
        return tileM.isGrassTile(newTileCol, newTileRow);
    }


    public Orc1(int x, int y, int size, Image runSpriteSheet, Image attackSpriteSheet, tileManager tileM) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.runSpriteSheet = runSpriteSheet;
        this.attackSpriteSheet = attackSpriteSheet;
        this.tileM = tileM;  // Make sure this line exists
        updateCurrentSprite();
    }

    private void updateCurrentSprite() {
        try {
            int frameWidth = 64;
            int frameHeight = 64;

            // Select the appropriate sprite sheet
            Image currentSheet = isAttacking ? attackSpriteSheet : runSpriteSheet;

            // Calculate the correct frame position
            int col = currentFrame;
            int row = facing.row;

            if (currentSheet != null && currentSheet.getPixelReader() != null) {
                currentSprite = new WritableImage(
                        currentSheet.getPixelReader(),
                        col * frameWidth,
                        row * frameHeight,
                        frameWidth,
                        frameHeight
                );
            }
        } catch (Exception e) {
            System.err.println("Error updating sprite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void draw(GraphicsContext gc, int cameraX, int cameraY) {
        if (isDead) {
            return; // Don't draw if dead
        }

        int drawX = (int)x - cameraX;
        int drawY = (int)y - cameraY;

        if (drawX + size >= 0 && drawX <= gc.getCanvas().getWidth() &&
                drawY + size >= 0 && drawY <= gc.getCanvas().getHeight()) {
            gc.drawImage(currentSprite, drawX, drawY, size, size);
        }
    }

    // Getter methods
    public double getX() { return x; }
    public double getY() { return y; }
    public int getSize() { return size; }
    public boolean isAttacking() { return isAttacking; }

    // Setter method for position
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}