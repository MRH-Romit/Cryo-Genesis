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

            tile[2] = new tile() ;
            tile[2].image = new Image(getClass().getResourceAsStream("/tiles/whole.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMap() {
        try {
            InputStream is = getClass().getResourceAsStream("/maps/map01.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
                String line = br.readLine();
                String[] numbers = line.split(" ");

                while (col < gp.maxScreenCol) {
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                    col++;
                }
                if (col == gp.maxScreenCol) {
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(GraphicsContext gc) {
        int col = 0;
        int row = 0;
        double x = 0;
        double y = 0;

        while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
            int tileNum = mapTileNum[col][row];
            gc.drawImage(tile[tileNum].image, x, y, gp.tileSize, gp.tileSize);

            col++;
            x += gp.tileSize;

            if (col == gp.maxScreenCol) {
                col = 0;
                x = 0;
                row++;
                y += gp.tileSize;
            }
        }
    }
}
