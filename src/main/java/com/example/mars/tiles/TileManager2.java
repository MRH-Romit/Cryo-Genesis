package com.example.mars.tiles;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.InputStream;

public class TileManager2 {

    private final int[][] mapTileNum;
    private final int mapWidth;
    private final int mapHeight;
    private final Tile[] tile;
    private final int tileSize;

    public TileManager2(int tileSize) {
        this.tileSize = tileSize;

        this.mapTileNum = new int[][] {
                {1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0},
                {1, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0},
                {1, 4, 4, 1, 1, 1, 4, 4, 1, 1, 1, 0},
                {1, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0},
                {1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        };

        this.mapWidth = mapTileNum[0].length;
        this.mapHeight = mapTileNum.length;

        tile = new Tile[10];
        loadTileImages();
    }

    private static class Tile {
        public Image image;
        public boolean collision = false;
        public boolean isGrass = false;
    }

    private void loadTileImages() {
        try {
            // Ground tile
            tile[0] = new Tile();
            tile[0].image = loadImage("/Map2/Map_Wall.png");
            tile[0].collision = false;

            // Wall tile
            tile[1] = new Tile();
            tile[1].image = loadImage("/Map2/Map_Wall_2.png");
            tile[1].collision = true; // Walls should have collision

            // Water tile
            tile[2] = new Tile();
            tile[2].image = loadImage("/Map2/MapWater.png");
            tile[2].collision = true; // Water should have collision

            // Water tile
            tile[3] = new Tile();
            tile[3].image = loadImage("/Map2/Map_Water.png");
            tile[3].collision = true;

            // Tree tile
            tile[4] = new Tile();
            tile[4].image = loadImage("/Map2/MapTree.png");
            tile[4].collision = true;

            // Grass tile
            tile[5] = new Tile();
            tile[5].image = loadImage("/Map2/mapGr.jpeg");
            tile[5].collision = false;
            tile[5].isGrass = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isCollision(int col, int row) {
        if (col >= 0 && col < mapWidth && row >= 0 && row < mapHeight) {
            int tileNum = mapTileNum[row][col];
            return tileNum >= 0 && tileNum < tile.length && tile[tileNum].collision;
        }
        return true; // Consider out-of-bounds as collision
    }

    public boolean isGrassTile(int col, int row) {
        if (col >= 0 && col < mapWidth && row >= 0 && row < mapHeight) {
            int tileNum = mapTileNum[row][col];
            return tileNum >= 0 && tileNum < tile.length && tile[tileNum].isGrass;
        }
        return false;
    }

    public int getTileTypeAt(int col, int row) {
        if (col >= 0 && col < mapWidth && row >= 0 && row < mapHeight) {
            return mapTileNum[row][col];
        }
        return -1;
    }

    private Image loadImage(String path) {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            System.err.println("Error: Could not load image from path: " + path);
            return null;
        }
        return new Image(is);
    }

    public void draw(GraphicsContext gc, int cameraX, int cameraY) {
        int startCol = Math.max(0, cameraX / tileSize);
        int endCol = Math.min(mapWidth, (cameraX + 960) / tileSize + 1);
        int startRow = Math.max(0, cameraY / tileSize);
        int endRow = Math.min(mapHeight, (cameraY + 640) / tileSize + 1);

        for (int row = startRow; row < endRow; row++) {
            for (int col = startCol; col < endCol; col++) {
                int tileNum = mapTileNum[row][col];
                int tileX = col * tileSize - cameraX;
                int tileY = row * tileSize - cameraY;

                if (tileNum >= 0 && tileNum < tile.length && tile[tileNum].image != null) {
                    gc.drawImage(tile[tileNum].image, tileX, tileY, tileSize, tileSize);
                }
            }
        }
    }

    // Getter methods
    public int getMapWidth() { return mapWidth; }
    public int getMapHeight() { return mapHeight; }
    public int getTileSize() { return tileSize; }
}