package com.example.mars.tiles;

import com.example.mars.GamePanel;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class tileManager {

   // public int mapHeight;
    //public int mapHeight;
    GamePanel gp;
    tile[] tile;
    int[][] mapTileNum;
    public int mapWidth = 20;  // Define map width (number of tiles horizontally)
   public int mapHeight = 20; // Define map height (number of tiles vertically)

    public tileManager(GamePanel gp) {
        this.gp = gp;
        tile = new tile[10];
        mapTileNum = new int[mapWidth][mapHeight]; // Adjusted for full map size
        getTileImage();
        loadMap();
    }

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

    public void draw(GraphicsContext gc, int cameraX, int cameraY) {
        // Determine the starting and ending tiles based on the camera position
        int startCol = Math.max(0, cameraX / gp.tileSize);
        int endCol = Math.min(mapWidth, (cameraX + gp.maxScreenCol * gp.tileSize) / gp.tileSize + 1);
        int startRow = Math.max(0, cameraY / gp.tileSize);
        int endRow = Math.min(mapHeight, (cameraY + gp.maxScreenRow * gp.tileSize) / gp.tileSize + 1);

        // Draw visible tiles
        for (int row = startRow; row < endRow; row++) {
            for (int col = startCol; col < endCol; col++) {
                int tileNum = mapTileNum[col][row];
                int tileX = col * gp.tileSize - cameraX;
                int tileY = row * gp.tileSize - cameraY;

                gc.drawImage(tile[tileNum].image, tileX, tileY, gp.tileSize, gp.tileSize);
            }
        }
    }
}
