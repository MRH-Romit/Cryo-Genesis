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
    public int mapWidth;
    public int mapHeight;

    public tileManager(GamePanel gp) {
        this.gp = gp;
        tile = new tile[10]; // Array for tile types
        getTileImage();
        loadMap();
    }

    public void getTileImage() {
        try {
            tile[0] = new tile();
            tile[0].image = loadImage("/tiles/flat.png");

            tile[1] = new tile();
            tile[1].image = loadImage("/tiles/wall.png");

            tile[2] = new tile();
            tile[2].image = loadImage("/tiles/water.png");

            tile[3] = new tile();
            tile[3].image = loadImage("/tiles/whole.png");

            tile[4] = new tile();
            tile[4].image = loadImage("/tiles/twoWhole.png");

            tile[5] = new tile();
            tile[5].image = loadImage("/tiles/hill.png");
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

    public void loadMap() {
        try (InputStream is = getClass().getResourceAsStream("/maps/world01.txt")) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

                String line = br.readLine();
                if (line == null) throw new IOException("Empty map file!");

                String[] firstRow = line.split(" ");
                mapWidth = firstRow.length;

                mapHeight = 1;
                while (br.readLine() != null) mapHeight++;

                mapTileNum = new int[mapWidth][mapHeight];

                is.close();
                try (InputStream newIs = getClass().getResourceAsStream("/maps/world01.txt");
                     BufferedReader newBr = new BufferedReader(new InputStreamReader(newIs))) {

                    for (int row = 0; row < mapHeight; row++) {
                        line = newBr.readLine();
                        if (line == null) break;

                        String[] numbers = line.split(" ");
                        for (int col = 0; col < mapWidth; col++) {
                            int tileIndex = Integer.parseInt(numbers[col]);
                            mapTileNum[col][row] = (tileIndex >= 0 && tileIndex < tile.length) ? tileIndex : 0;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
