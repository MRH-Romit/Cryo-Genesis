// TileManager2 Class (Unchanged from your provided code)
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
                {1, 1, 1, 0, 0, 0, 1, 6, 1, 0, 0, 0, 1, 0, 0, 0},
                {1, 5, 5, 5, 5, 5, 4, 6, 4, 4, 4, 0, 4, 4, 4, 1},
                {1, 5, 5, 2, 5, 5, 4, 6, 4, 1, 1, 0, 4, 4, 4, 0},
                {1, 5, 5, 5, 5, 5, 4, 6, 4, 4, 4, 0, 4, 4, 4, 0},
                {1, 4, 4, 4, 4, 4, 4, 6, 4, 0, 0, 0, 4, 4, 4, 0},
                {0, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 1},
                {0, 4, 4, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 4, 1},
                {0, 0, 0, 0, 0, 0, 4, 6, 4, 0, 0, 0, 4, 4, 4, 1},
                {0, 0, 0, 0, 0, 0, 4, 6, 4, 0, 0, 0, 1, 1, 0, 1},
        };

        this.mapWidth = mapTileNum[0].length;
        this.mapHeight = mapTileNum.length;

        tile = new Tile[10];
        loadTileImages();
    }


    private static class Tile {
        public Image image;
        public boolean collision = false;
    }

    private void loadTileImages() {
        try {
            tile[0] = new Tile();
            tile[0].image = loadImage("/Map2/Map_Wall_2.png");

            tile[1] = new Tile();
            tile[1].image = loadImage("/Map2/Map_Wall.png");
            tile[1].collision = true;

            tile[2] = new Tile();
            tile[2].image = loadImage("/Map2/MapWater.png");
            tile[2].collision = true;

            tile[4] = new Tile();
            tile[4].image = loadImage("/Map2/Map_Tree.png");
            tile[4].collision = true;

            tile[5] = new Tile();
            tile[5].image = loadImage("/Map2/mapGr.jpeg");

            tile[6] = new Tile();
            tile[6].image = loadImage("/Map2/Map_tree_Gora.png");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Image loadImage(String path) {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            throw new RuntimeException("Error loading image: " + path);
        }
        return new Image(is);
    }

    public void draw(GraphicsContext gc, int cameraX, int cameraY) {
        for (int row = 0; row < mapHeight; row++) {
            for (int col = 0; col < mapWidth; col++) {
                int tileNum = mapTileNum[row][col];
                int x = col * tileSize - cameraX;
                int y = row * tileSize - cameraY;

                if (tileNum >= 0 && tileNum < tile.length && tile[tileNum].image != null) {
                    gc.drawImage(tile[tileNum].image, x, y, tileSize, tileSize);
                }
            }
        }
    }

    public boolean isCollision(int col, int row) {
        if (col < 0 || row < 0 || col >= mapWidth || row >= mapHeight) {
            return true;
        }
        int tileNum = mapTileNum[row][col];
        return tile[tileNum].collision;
    }
}
