package com.example.mars.puzzle1;

import javafx.scene.layout.VBox;

import java.util.Stack;

public class Tower {

    public VBox tower;
    public Stack<Block> towerContent;

    Tower(VBox tower) {
        this.tower = tower;
        this.towerContent = new Stack<>();
    }

    public void addBlock(Block BLOCK) {
        this.towerContent.push(BLOCK);
        this.tower.getChildren().add(BLOCK.block);
    }

    public void moveToTower(Tower T) {
        T.addBlock(this.towerContent.pop());
    }

    public void clearTower() {

        if (this.towerContent.isEmpty()) {
            return;
        }
        this.towerContent.clear();
        this.tower.getChildren().clear();
    }
}
