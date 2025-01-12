package com.example.mars.tiles;

import com.example.mars.GamePanel;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class tileManager {
    private Image flat, flooring, twoWhole, wall, bigblueWall, water, woodenDoor;
    private int[][] map;
    private final int tileSize = 64; // Ensure consistent tile size

    public tileManager(GamePanel gp) {
        flat = new Image(getClass().getResourceAsStream("/tiles/flat.png"), tileSize, tileSize, false, false);
        flooring = new Image(getClass().getResourceAsStream("/tiles/flooring.png"), tileSize, tileSize, false, false);
        twoWhole = new Image(getClass().getResourceAsStream("/tiles/twoWhole.png"), tileSize, tileSize, false, false);
        wall = new Image(getClass().getResourceAsStream("/tiles/wall.png"), tileSize, tileSize, false, false);
        bigblueWall = new Image(getClass().getResourceAsStream("/tiles/bigblueWall.png"), tileSize, tileSize, false, false);
        water = new Image(getClass().getResourceAsStream("/tiles/Water.png"), tileSize, tileSize, false, false);
        woodenDoor = new Image(getClass().getResourceAsStream("/tiles/wooden_door.png"), tileSize, tileSize, false, false);

        // Example map data
        map = new int[][] {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 2, 2, 0, 0, 3, 3, 2, 2, 0, 0, 1},
                {1, 4, 4, 0, 2, 2, 0, 4, 0, 0, 0, 1},
                {1, 4, 0, 0, 2, 2, 0, 3, 3, 4, 0, 1},
                {1, 3, 0, 4, 0, 0, 4, 2, 2, 2, 2, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };
    }

    public void draw(GraphicsContext gc) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                int tile = map[y][x];
                Image tileImage;

                switch (tile) {
                    case 1:
                        tileImage = wall;
                        break;
                    case 2:
                        tileImage = flooring;
                        break;
                    case 3:
                        tileImage = twoWhole;
                        break;
                    case 4:
                        tileImage = bigblueWall;
                        break;
                    case 5:
                        tileImage = water;
                        break;
                    case 6:
                        tileImage = woodenDoor;
                        break;
                    default:
                        tileImage = flat;
                        break;
                }

                // Draw each tile at the correct position
                gc.drawImage(tileImage, x * tileSize, y * tileSize);
            }
        }
    }
}
