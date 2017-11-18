package main.java.gui;


import java.util.HashMap;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import main.java.impl.Position;

// This represents a tile on the game board,
// A tile can either hold a piece or hold nothing
// A tile has a position
public class Tile extends Rectangle {

    public static int HEIGHT = 70;
    public static int WIDTH = 70;

    public static String TL = "tl";
    public static String TR = "tr";
    public static String BL = "bl";
    public static String BR = "br";
    public static String NONE = "NONE";

    private int posX;
    private int posY;
    private HashMap<String, Position> surrounding;

    // Represents the colour of the tile
    private TileType type;

    // Reference to piece if this tile has one
    private Piece piece;

    public Tile(int posX, int posY, TileType type) {
        this.posX = posX;
        this.posY = posY;
        this.type = type;
        piece = null;

        surrounding = new HashMap<>();

        if (posX!=0 && posY!= 0) {
            surrounding.put(TL, new Position(posX-1, posY-1));
        } else {
            surrounding.put(TL, null);
        }

        if (posX!=Board.HEIGHT-1 && posY!=Board.WIDTH-1) {
            surrounding.put(TR, new Position(posX+1, posY-1));
        } else {
            surrounding.put(TR, null);
        }
        surrounding.put(BL, new Position(posX-1, posY+1));
        surrounding.put(BR, new Position(posX+1, posY+1));

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

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Piece getPiece() {
        return piece;
    }

    public boolean hasPiece() {
        return piece != null;
    }

    public void removePiece() {
        this.piece = null;
    }

    public HashMap<String, Position> getSurrounding() {
        return surrounding;
    }
}
