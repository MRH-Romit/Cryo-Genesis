package com.example.mars.tiles;

import javafx.scene.image.Image;

public class tile {
    public Image image;      // The image representing the tile
    public boolean collision; // Indicates if the tile causes collision (non-walkable)

    public tile() {
        this.image = null;
        this.collision = false; // Default: Walkable tile
    }

    public tile(Image image, boolean collision) {
        this.image = image;
        this.collision = collision;
    }
}
