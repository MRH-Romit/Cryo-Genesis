package com.example.mars.tiles;

import javafx.scene.image.Image;

public class tile {
    public Image image;
    public boolean collision;
    public boolean isGrass; // Add this to identify grass tiles specifically

    public tile() {
        this.image = null;
        this.collision = false;
        this.isGrass = false;
    }

    public tile(Image image, boolean collision, boolean isGrass) {
        this.image = image;
        this.collision = collision;
        this.isGrass = isGrass;
    }
}