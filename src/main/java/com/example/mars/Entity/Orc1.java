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
    private final long frameDuration = 150_000_000;
    private int detectionRange = 150; // Reduced for closer detection
    private int attackRange = 80;     // Reduced for closer attack
    private double moveSpeed = 2.0;   // Adjusted for smoother movement
    private Direction facing = Direction.DOWN;
    private tileManager tileM;
    private boolean isMoving = false;

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

    public Orc1(int x, int y, int size, Image runSpriteSheet, Image attackSpriteSheet, tileManager tileM) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.runSpriteSheet = runSpriteSheet;
        this.attackSpriteSheet = attackSpriteSheet;
        this.tileM = tileM;
        updateCurrentSprite();
    }

    public void update(int heroX, int heroY, long currentNanoTime) {
        // Calculate distance and direction to hero
        double dx = heroX - x;
        double dy = heroY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Debug print
        System.out.println("Distance to hero: " + distance);
        System.out.println("Current position: " + x + ", " + y);
        System.out.println("Hero position: " + heroX + ", " + heroY);

        // Check if both Orc and Hero are in grass tiles
        int orcTileCol = (int) (x / size);
        int orcTileRow = (int) (y / size);
        int heroTileCol = heroX / size;
        int heroTileRow = heroY / size;

        boolean bothInGrass = tileM.isGrassTile(orcTileCol, orcTileRow) &&
                tileM.isGrassTile(heroTileCol, heroTileRow);

        // Update facing direction based on hero position
        if (Math.abs(dx) > Math.abs(dy)) {
            facing = dx > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            facing = dy > 0 ? Direction.DOWN : Direction.UP;
        }

        if (bothInGrass && distance <= detectionRange) {
            if (distance <= attackRange) {
                // Close enough to attack
                isAttacking = true;
                isMoving = false;
                System.out.println("Attacking!");
            } else {
                // Move towards hero
                isAttacking = false;
                isMoving = true;

                // Calculate movement
                double angle = Math.atan2(dy, dx);
                double moveX = Math.cos(angle) * moveSpeed;
                double moveY = Math.sin(angle) * moveSpeed;

                // Calculate new position
                double newX = x + moveX;
                double newY = y + moveY;

                // Check if new position is valid
                int newTileCol = (int) (newX / size);
                int newTileRow = (int) (newY / size);

                if (tileM.isGrassTile(newTileCol, newTileRow) &&
                        !tileM.isCollision(newTileCol, newTileRow)) {
                    x = newX;
                    y = newY;
                    System.out.println("Moving towards hero!");
                }
            }
        } else {
            // Reset state when hero is out of range or not in grass
            isAttacking = false;
            isMoving = false;
        }

        // Update animation
        if (currentNanoTime - lastFrameTime > frameDuration) {
            if (isAttacking) {
                // Use attack animation frames
                currentFrame = (currentFrame + 1) % 8;
            } else if (isMoving) {
                // Use running animation frames
                currentFrame = (currentFrame + 1) % 8;
            } else {
                // Idle animation or reset to first frame
                currentFrame = 0;
            }
            lastFrameTime = currentNanoTime;
            updateCurrentSprite();
        }
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
        int drawX = (int)x - cameraX;
        int drawY = (int)y - cameraY;

        if (drawX + size >= 0 && drawX <= gc.getCanvas().getWidth() &&
                drawY + size >= 0 && drawY <= gc.getCanvas().getHeight()) {

            // Draw the sprite
            gc.drawImage(currentSprite, drawX, drawY, size, size);

            // Debug: Draw detection and attack ranges
            gc.setStroke(Color.YELLOW);
            gc.strokeOval(drawX - detectionRange + size/2, drawY - detectionRange + size/2,
                    detectionRange * 2, detectionRange * 2);
            gc.setStroke(Color.RED);
            gc.strokeOval(drawX - attackRange + size/2, drawY - attackRange + size/2,
                    attackRange * 2, attackRange * 2);
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