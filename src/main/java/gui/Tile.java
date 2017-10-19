package main.java.gui;


import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle {

    public static int HEIGHT = 100;
    public static int WIDTH = 100;

    private int posX;
    private int posY;
    private TileType type;

    public Tile(int posX, int posY, TileType type) {
        this.posX = posX;
        this.posY = posY;
        this.type = type;

        // The size of each rectangle
        setHeight(HEIGHT);
        setWidth(WIDTH);

        // Where the rectangle is on the board.
        // Multiply WIDTH and HEIGHT by x or y to get actual pixel value
        relocate(posX * HEIGHT, posY * WIDTH);

        // Choose colour
        if (type.equals(TileType.BROWN)) {
            setFill(Paint.valueOf("#d18b47"));
        } else {
            setFill(Paint.valueOf("#ffce9e"));
        }
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public TileType getType() {
        return type;
    }
}
