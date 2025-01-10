package com.example.mars.tiles;

import com.example.mars.GamePanel;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class tileManager {

    GamePanel gp;
    tile[] tile;
    int[][] mapTileNum;

    public tileManager(GamePanel gp) {
        this.gp = gp;
        tile = new tile[10];
        mapTileNum = new int[gp.maxScreenCol][gp.maxScreenRow];
        getTileImage();
        loadMap();
    }

    public void getTileImage() {
        try {
            tile[0] = new tile();
            tile[0].image = new Image(getClass().getResourceAsStream("/tiles/ground.png"));

            tile[1] = new tile();
            tile[1].image = new Image(getClass().getResourceAsStream("/tiles/wall.png"));

            tile[2] = new tile();
            tile[2].image = new Image(getClass().getResourceAsStream("/tiles/water.png")); // Example

            tile[3] = new tile();
            tile[3].image = new Image(getClass().getResourceAsStream("/tiles/lava.png")); // Example
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMap() {
        try (InputStream is = getClass().getResourceAsStream("/maps/map01.txt");
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            for (int row = 0; row < gp.maxScreenRow; row++) {
                String line = br.readLine();
                if (line == null) break; // Stop if no more rows
                String[] numbers = line.split(" ");
                for (int col = 0; col < gp.maxScreenCol; col++) {
                    if (col < numbers.length) {
                        mapTileNum[col][row] = Integer.parseInt(numbers[col]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(GraphicsContext gc) {
        for (int row = 0; row < gp.maxScreenRow; row++) {
            for (int col = 0; col < gp.maxScreenCol; col++) {
                int tileNum = 0; // Default to ground if tile is missing
                if (col < mapTileNum.length && row < mapTileNum[col].length) {
                    tileNum = mapTileNum[col][row];
                }
                gc.drawImage(tile[tileNum].image, col * gp.tileSize, row * gp.tileSize, gp.tileSize, gp.tileSize);
            }
        }
    }
} // Add this closing curly brace to fix the error
