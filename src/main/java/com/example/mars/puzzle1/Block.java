package com.example.mars.puzzle1;

import javafx.scene.shape.Rectangle;

public class Block {

    public Rectangle block;
    public int width;

    Block(Rectangle block, int width) {

        this.width = width;
        this.block = block;
    }
}
