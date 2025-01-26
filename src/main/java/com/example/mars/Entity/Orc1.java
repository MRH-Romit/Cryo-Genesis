package com.example.mars.Entity;

import com.example.mars.tiles.tileManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.List;

public class Orc1 {
    private double x, y;
    private int size;
    private Image runSpriteSheet;
    private Image attackSpriteSheet;
    private WritableImage currentSprite;
    private int currentFrame = 0;
    private int framesPerRow = 8;
    private boolean isAttacking = false;
    private long lastFrameTime = 0;
    private final long frameDuration = 100_000_000;
    private int detectionRange = 300; // Orc detects hero within 300px
    private int attackRange = 80;     // Orc attacks hero within 80px
    private double moveSpeed = 2.0;   // Orc movement speed
    private boolean isMoving = false;
    private int health = 5;
    private boolean isDead = false;
    private int minimumDistance = 50; // Minimum distance between orcs
    private tileManager tileM;

    public Orc1(double x, double y, int size, Image runSpriteSheet, Image attackSpriteSheet, tileManager tileM) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.runSpriteSheet = runSpriteSheet;
        this.attackSpriteSheet = attackSpriteSheet;
        this.tileM = tileM;
        updateCurrentSprite();
    }

    public void update(double heroX, double heroY, long currentNanoTime, List<Orc1> orcs) {
        if (isDead) return;

        double dx = heroX - x;
        double dy = heroY - y;
        double distanceToHero = Math.sqrt(dx * dx + dy * dy);

        if (distanceToHero <= detectionRange) {
            if (distanceToHero <= attackRange) {
                isAttacking = true;
                isMoving = false; // Stop moving while attacking
            } else {
                isAttacking = false;
                isMoving = true;

                double angle = Math.atan2(dy, dx);
                double moveX = Math.cos(angle) * moveSpeed;
                double moveY = Math.sin(angle) * moveSpeed;

                double newX = x + moveX;
                double newY = y + moveY;

                // Check for collision and minimum distance with other orcs
                if (isValidPosition(newX, newY) && !isOverlapping(newX, newY, orcs)) {
                    x = newX;
                    y = newY;
                }
            }
        } else {
            isAttacking = false;
            isMoving = false;
        }

        // Update animation frames
        if (currentNanoTime - lastFrameTime > frameDuration) {
            if (isAttacking) {
                currentFrame = (currentFrame + 1) % framesPerRow; // Cycle attack frames
            } else if (isMoving) {
                currentFrame = (currentFrame + 1) % framesPerRow; // Cycle run frames
            } else {
                currentFrame = 0; // Idle frame
            }
            lastFrameTime = currentNanoTime;
            updateCurrentSprite();
        }
    }

    private boolean isOverlapping(double newX, double newY, List<Orc1> orcs) {
        for (Orc1 other : orcs) {
            if (other != this && !other.isDead) {
                double distance = Math.sqrt(Math.pow(newX - other.x, 2) + Math.pow(newY - other.y, 2));
                if (distance < minimumDistance) {
                    return true; // Overlap detected
                }
            }
        }
        return false;
    }

    private boolean isValidPosition(double newX, double newY) {
        int newTileCol = (int) (newX / size);
        int newTileRow = (int) (newY / size);
        return tileM.isGrassTile(newTileCol, newTileRow);
    }

    public void draw(GraphicsContext gc, int cameraX, int cameraY) {
        if (isDead) return;

        int drawX = (int) x - cameraX;
        int drawY = (int) y - cameraY;

        if (drawX + size >= 0 && drawX <= gc.getCanvas().getWidth() &&
                drawY + size >= 0 && drawY <= gc.getCanvas().getHeight()) {
            // Use attack sprite if attacking, otherwise use run sprite
            Image spriteToDraw = isAttacking ? attackSpriteSheet : runSpriteSheet;

            gc.drawImage(currentSprite, drawX, drawY, size * 1.5, size * 1.5); // Increased size
        }
    }

    private void updateCurrentSprite() {
        int frameWidth = 64;
        int frameHeight = 64;
        int col = currentFrame;
        int row = isAttacking ? 1 : 0;

        if (runSpriteSheet.getPixelReader() != null) {
            currentSprite = new WritableImage(
                    runSpriteSheet.getPixelReader(),
                    col * frameWidth,
                    row * frameHeight,
                    frameWidth,
                    frameHeight
            );
        }
    }

    // Getter for x
    public double getX() {
        return x;
    }

    // Getter for y
    public double getY() {
        return y;
    }

    // Setter for position
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Method to check if hero is in attack range
    public boolean isHeroInAttackRange(double heroX, double heroY) {
        double dx = heroX - x;
        double dy = heroY - y;
        double distanceToHero = Math.sqrt(dx * dx + dy * dy);

        return isAttacking && distanceToHero <= attackRange;
    }
}
