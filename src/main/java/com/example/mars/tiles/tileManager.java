package com.example.mars.tiles;

import com.example.mars.GamePanel;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class tileManager {

    private GamePanel gp;
    private tile[] tile;
    public int[][] mapTileNum;
    public int mapWidth = 30;  // Define map width (number of tiles horizontally)
    public int mapHeight = 30; // Define map height (number of tiles vertically)

    public tileManager(GamePanel gp) {
        this.gp = gp;
        tile = new tile[10]; // Array to hold different tile types
        mapTileNum = new int[mapWidth][mapHeight]; // 2D array for the map layout
        getTileImage(); // Load tile images
        loadMap(); // Load map data from file
    }

    // Method to load tile images
    public void getTileImage() {
        try {
            tile[0] = new tile();
            tile[0].image = new Image(getClass().getResourceAsStream("/tiles/flat.png"));

            tile[1] = new tile();
            tile[1].image = new Image(getClass().getResourceAsStream("/tiles/wall.png"));

            tile[2] = new tile();
            tile[2].image = new Image(getClass().getResourceAsStream("/tiles/water.png"));

            tile[3] = new tile();
            tile[3].image = new Image(getClass().getResourceAsStream("/tiles/whole.png"));

            tile[4] = new tile();
            tile[4].image = new Image(getClass().getResourceAsStream("/tiles/twoWhole.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to load the map from a text file
    public void loadMap() {
        try (InputStream is = getClass().getResourceAsStream("/maps/world01.txt");
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            for (int row = 0; row < mapHeight; row++) {
                String line = br.readLine();
                if (line == null) break; // Stop if no more rows
                String[] numbers = line.split(" ");
                for (int col = 0; col < mapWidth; col++) {
                    if (col < numbers.length) {
                        mapTileNum[col][row] = Integer.parseInt(numbers[col]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Draw method to render visible tiles based on the camera position
    public void draw(GraphicsContext gc, int cameraX, int cameraY) {
        int startCol = Math.max(0, cameraX / gp.tileSize);
        int endCol = Math.min(mapWidth, (cameraX + gp.maxScreenCol * gp.tileSize) / gp.tileSize + 1);
        int startRow = Math.max(0, cameraY / gp.tileSize);
        int endRow = Math.min(mapHeight, (cameraY + gp.maxScreenRow * gp.tileSize) / gp.tileSize + 1);

        for (int row = startRow; row < endRow; row++) {
            for (int col = startCol; col < endCol; col++) {
                int tileNum = mapTileNum[col][row];
                int tileX = col * gp.tileSize - cameraX;
                int tileY = row * gp.tileSize - cameraY;

                if (tileNum >= 0 && tileNum < tile.length) {
                    gc.drawImage(tile[tileNum].image, tileX, tileY, gp.tileSize, gp.tileSize);
                }
            }
        }
    }
}
