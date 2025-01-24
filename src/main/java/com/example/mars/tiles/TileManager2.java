package com.example.mars.tiles;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.InputStream;

public class TileManager2 {

    private final int[][] mapTileNum; // Map layout
    private final int mapWidth;       // Map width in tiles
    private final int mapHeight;      // Map height in tiles
    private final Tile[] tile;        // Array of tiles
    private final int tileSize;       // Size of each tile

    public TileManager2(int tileSize) {
        this.tileSize = tileSize;

        // Define the map layout directly here
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

        tile = new Tile[10]; // Adjust number of tile types as needed
        loadTileImages();
    }

    private void loadTileImages() {
        try {
            tile[0] = new Tile();
            tile[0].image = loadImage("/Map2/Map_Ground.png"); // Ground tile

            tile[1] = new Tile();
            tile[1].image = loadImage("/Map2/Map_Wall_1.png"); // Wall tile

            tile[2] = new Tile();
            tile[2].image = loadImage("/Map2/MapWater.png"); // Water tile

            tile[3] = new Tile();
            tile[3].image = loadImage("/Map2/Map_Water.png"); // Water tile







        } catch (Exception e) {
            e.printStackTrace();
        }
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
        int endCol = Math.min(mapWidth, (cameraX + 960) / tileSize + 1); // Adjust screen width (960)
        int startRow = Math.max(0, cameraY / tileSize);
        int endRow = Math.min(mapHeight, (cameraY + 640) / tileSize + 1); // Adjust screen height (640)

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

    private static class Tile {
        public Image image;
    }
}
