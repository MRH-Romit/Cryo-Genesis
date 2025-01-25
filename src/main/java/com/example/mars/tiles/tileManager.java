package com.example.mars.tiles;

import com.example.mars.GamePanel;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.InputStream;

public class tileManager {

    private final GamePanel gp;
    private final tile[] tile;
    public final int[][] mapTileNum; // Map array holding tile indices
    public final int mapWidth; // Number of tiles horizontally
    public final int mapHeight; // Number of tiles vertically

    public tileManager(GamePanel gp) {
        this.gp = gp;

        // Define map dimensions
        this.mapWidth = 20;  // 20 tiles wide
        this.mapHeight = 20; // 20 tiles high
        this.mapTileNum = new int[mapHeight][mapWidth]; // 2D array for map layout

        tile = new tile[10]; // Array to store up to 10 tile types
        getTileImage(); // Load tile images
        loadMap(); // Define the map layout
    }

    public void getTileImage() {
        try {
            tile[0] = new tile();
            tile[0].image = loadImage("/obj/chest.png"); // Empty tile
            tile[0].collision = false;  // No collision

            tile[1] = new tile();
            tile[1].image = loadImage("/Map1/Map_Wall_3.png"); // Wall tile
            tile[1].collision = true;  // Only wall has collision

            tile[2] = new tile();
            tile[2].image = loadImage("/Map1/mapGr.png"); // Ground/Grass tile
            tile[2].collision = false;  // No collision
            tile[2].isGrass = true;

            tile[3] = new tile();
            tile[3].image = loadImage("/Map1/mapRock.png"); // Rock tile
            tile[3].collision = false;  // Changed to false

            tile[4] = new tile();
            tile[4].image = loadImage("/Map1/mapTree.png"); // Tree tile
            tile[4].collision = false;  // Changed to false

            tile[5] = new tile();
            tile[5].image = loadImage("/Map1/mapWay.png"); // Path tile
            tile[5].collision = false;

            tile[6] = new tile();
            tile[6].image = loadImage("/obj/door.png"); // Door tile
            tile[6].collision = false;  // Changed to false

            tile[7] = new tile();
            tile[7].image = loadImage("/Map1/Map_Wall_Art_1.png");
            tile[7].collision = false;  // Changed to false

            tile[8] = new tile();
            tile[8].image = loadImage("/Map1/space.png");
            tile[8].collision = false;

            tile[9] = new tile();
            tile[9].image = loadImage("/Map1/Map_Wall_Art_2.png");
            tile[9].collision = false;  // Changed to false

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Add these helper methods
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
        return -1; // Return -1 for invalid positions
    }

    private Image loadImage(String path) {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            System.err.println("Error: Could not load image from path: " + path);
            return null;
        }
        return new Image(is);
    }

    public void loadMap() {
        // Define a 2D array to represent the map layout
        int[][] predefinedMap = {
                {1, 1, 1, 9, 1, 9, 1, 1, 1, 7, 1, 1, 9, 9, 7, 9, 1, 9, 1, 1},
                {1, 4, 4, 4, 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 9},
                {1, 1, 1, 9, 1, 1, 1, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 4, 4, 1},
                {9, 2, 2, 2, 2, 0, 1, 4, 4, 5, 4, 4, 4, 2, 2, 2, 4, 4, 4, 1},
                {1, 2, 2, 2, 2, 2, 1, 4, 4, 5, 4, 3, 4, 2, 2, 2, 4, 4, 4, 1},
                {1, 2, 2, 2, 2, 2, 1, 4, 4, 5, 4, 4, 4, 2, 2, 2, 4, 4, 4, 1},
                {9, 2, 2, 2, 2, 2, 1, 4, 4, 5, 4, 4, 4, 4, 4, 4, 3, 4, 4, 1},
                {1, 1, 1, 6, 6, 1, 1, 3, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 9},
                {1, 4, 4, 5, 4, 4, 3, 3, 4, 5, 4, 4, 4, 3, 3, 3, 4, 4, 4, 9},
                {1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 1},
                {1, 3, 3, 3, 3, 3, 3, 3, 3, 5, 8, 2, 2, 2, 5, 3, 3, 3, 3, 1},
                {9, 2, 4, 3, 3, 3, 3, 4, 2, 5, 3, 3, 3, 3, 5, 2, 2, 2, 2, 1},
                {1, 3, 3, 3, 3, 3, 3, 3, 3, 5, 3, 3, 1, 1, 9, 1, 1, 1, 2, 1},
                {1, 3, 3, 3, 3, 3, 3, 3, 3, 5, 3, 3, 1, 4, 4, 4, 4, 1, 2, 1},
                {9, 3, 3, 3, 3, 3, 3, 3, 3, 5, 3, 3, 9, 4, 2, 2, 4, 1, 2, 1},
                {1, 3, 3, 3, 3, 3, 3, 3, 3, 5, 3, 3, 1, 4, 2, 2, 4, 9, 2, 1},
                {1, 3, 3, 3, 3, 3, 3, 3, 3, 5, 3, 3, 9, 4, 4, 4, 4, 1, 2, 1},
                {1, 3, 3, 3, 3, 3, 3, 3, 3, 5, 3, 3, 1, 1, 9, 7, 1, 1, 2, 1},
                {9, 3, 3, 3, 3, 3, 3, 3, 3, 5, 3, 3, 3, 3, 3, 4, 3, 3, 2, 9},
                {1, 1, 9, 1, 9, 1, 1, 9, 1, 7, 1, 9, 1, 1, 9, 1, 1, 1, 9, 9}
        };

        // Copy the predefinedMap into mapTileNum
        for (int row = 0; row < mapHeight; row++) {
            for (int col = 0; col < mapWidth; col++) {
                mapTileNum[row][col] = predefinedMap[row][col];
            }
        }
    }

    public void draw(GraphicsContext gc, int cameraX, int cameraY) {
        int startCol = Math.max(0, cameraX / gp.tileSize);
        int endCol = Math.min(mapWidth, (cameraX + gp.maxScreenCol * gp.tileSize) / gp.tileSize + 1);
        int startRow = Math.max(0, cameraY / gp.tileSize);
        int endRow = Math.min(mapHeight, (cameraY + gp.maxScreenRow * gp.tileSize) / gp.tileSize + 1);

        for (int row = startRow; row < endRow; row++) {
            for (int col = startCol; col < endCol; col++) {
                int tileNum = mapTileNum[row][col];
                int tileX = col * gp.tileSize - cameraX;
                int tileY = row * gp.tileSize - cameraY;

                if (tileNum >= 0 && tileNum < tile.length && tile[tileNum].image != null) {
                    gc.drawImage(tile[tileNum].image, tileX, tileY, gp.tileSize, gp.tileSize);
                }
            }
        }
    }
}
