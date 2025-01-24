package com.example.mars.Entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Orc {
    private int x, y; // Position of the orc
    private int size; // Size of the orc (tile size)
    private Image sprite; // Sprite for the orc

    public Orc(int x, int y, int size, Image sprite) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.sprite = sprite;
    }

    public void draw(GraphicsContext gc, int cameraX, int cameraY) {
        int drawX = x - cameraX;
        int drawY = y - cameraY;
        gc.drawImage(sprite, drawX, drawY, size, size);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
